import sys
import os
import requests
import base64
from langchain_ibm import WatsonxLLM
# -------------------
# Config
# -------------------
GITHUB_TOKEN = "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".strip()
REPO = "PR-review"
ORG = "shilpadas2511-cmd"
# Watsonx credentials (from secrets too, not hardcoded!)
WATSONX_APIKEY = "0naEz5glYS4UMnH6O_UCaGoCXUjTub00V1LdajsuKOzG"
WATSONX_PROJECT_ID = "6729461b-223c-4769-a525-821865221429"
WATSONX_URL = "https://us-south.ml.cloud.ibm.com"
MODEL_ID = "ibm/granite-3-8b-instruct"  # good balance for code review
# -------------------
# GitHub API - fetch PR files
# -------------------
if len(sys.argv) < 2:
    print("Usage: python pr_review_agent_watsonx.py <PR_NUMBER>")
    sys.exit(1)
PR_NUMBER = sys.argv[1]
pr_files_url = f"https://api.github.com/repos/{REPO}/pulls/{PR_NUMBER}/files"
headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}
files_data = requests.get(pr_files_url, headers=headers).json()
if "message" in files_data:
    print(f":x: GitHub API Error: {files_data['message']}")
    sys.exit(1)
java_files = []
for file in files_data:
    filename = file.get("filename")
    patch = file.get("patch")
    if filename and patch and filename.endswith(".java"):
        file_url = f"https://api.github.com/repos/{REPO}/contents/{filename}"
        file_resp = requests.get(file_url, headers=headers).json()
        if "content" in file_resp:
            full_code = base64.b64decode(file_resp["content"]).decode("utf-8")
        else:
            full_code = patch
        java_files.append({"filename": filename, "code": full_code})
if not java_files:
    print(":white_check_mark: No Java files changed in this PR.")
    sys.exit(0)
# -------------------
# Watsonx setup
# -------------------
llm = WatsonxLLM(
    model_id=MODEL_ID,
    url=WATSONX_URL,
    apikey=WATSONX_APIKEY,
    project_id=WATSONX_PROJECT_ID,
)
# -------------------
# Review each file
# -------------------
results = {}
for f in java_files:
    filename = f["filename"]
    code = f["code"]
    if len(code) > 4000:
        code = code[:4000] + "\n... (truncated)"
    prompt = f"""
You are a senior Java software architect and reviewer.
File: {filename}
Java Code:
{code}
Instructions:
1. Identify missing best-practice improvements in readability, maintainability, performance, security, testing, and design.
2. Provide suggestions in clear bullet points with severity [Low|Medium|High].
3. If the code is fine, say: 'No issues, code follows best practices.'
"""
    try:
        feedback = llm.invoke(prompt)
    except Exception as e:
        feedback = f":x: Watsonx call failed: {str(e)}"
    results[filename] = feedback
# -------------------
# Write GitHub Check Summary
# -------------------
summary_path = os.getenv("GITHUB_STEP_SUMMARY", "review_summary.md")
has_issues = False
with open(summary_path, "w") as f:
    f.write("## :robot_face: AI PR Review (Watsonx)\n\n")
    for filename, suggestions in results.items():
        f.write(f"### {filename}\n")
        f.write(f"{suggestions}\n\n")
        if "No issues" not in suggestions:
            has_issues = True
    if has_issues:
        f.write(":x: **Review failed**: Some files need improvements.\n")
    else:
        f.write(":white_check_mark: **Review passed**: All files follow best practices.\n")
# Exit with failure if issues found
if has_issues:
    sys.exit(1)
else:
    sys.exit(0)

