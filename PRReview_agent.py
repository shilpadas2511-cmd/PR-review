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
if len(sys.argv) < 2:
    print("Usage: python pr_review_agent_watsonx.py <PR_NUMBER>")
    sys.exit(1)
PR_NUMBER = sys.argv[1]
# -------------------
# GitHub API to fetch changed files
# -------------------
pr_files_url = f"https://api.github.com/repos/{ORG}/{REPO}/pulls/{PR_NUMBER}/files"
headers = {
    "Authorization": GITHUB_TOKEN,
    "Accept": "application/vnd.github.v3+json"
}
files_data = requests.get(pr_files_url, headers=headers).json()
if "message" in files_data:
    print(f":x: GitHub API Error: {files_data['message']}")
    sys.exit(1)
# Collect only Java files
java_files = []
for file in files_data:
    filename = file.get("filename")
    patch = file.get("patch")
    if filename and patch and filename.endswith(".java"):
        file_url = f"https://api.github.com/repos/{ORG}/{REPO}/contents/{filename}"
        file_resp = requests.get(file_url, headers=headers).json()
        if "content" in file_resp:
            full_code = base64.b64decode(file_resp["content"]).decode("utf-8")
        else:
            full_code = patch
        java_files.append({"filename": filename, "code": full_code})
if not java_files:
    print("No Java files changed in this PR.")
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
# Loop through changed Java files
# -------------------
all_feedback = []
for f in java_files:
    filename = f["filename"]
    code = f["code"]
    MAX_CODE_LENGTH = 4000
    if len(code) > MAX_CODE_LENGTH:
        code = code[:MAX_CODE_LENGTH] + "\n... (truncated)"
    prompt = f"""
You are a senior Java software architect and code reviewer.
File: {filename}
Java Code:
{code}
Instructions:
1. Identify missing **best-practice improvements** in this Java code.
2. Focus on readability, maintainability, performance, security, testing, and design patterns.
3. List suggestions in structured format:
- Suggestion 1 [Severity]
- Suggestion 2 [Severity]
- Suggestion 3 [Severity]
4. If the code follows best practices, write: "No suggestions, code follows best practices."
"""
    try:
        feedback = llm.invoke(prompt)
    except Exception as e:
        feedback = f":x: Watsonx call failed: {str(e)}"
    all_feedback.append(f"### File: `{filename}`\n{feedback}\n")
# -------------------
# Post Review Comment on PR
# -------------------
def post_comment(pr_number, body):
    url = f"https://api.github.com/repos/{ORG}/{REPO}/issues/{pr_number}/comments"
    payload = {"body": body}
    r = requests.post(url, headers=headers, json=payload)
    if r.status_code not in [200, 201]:
        print(f":x: Failed to post comment: {r.status_code}, {r.text}")
    else:
        print(":white_check_mark: Comment posted successfully.")
issues_found = any("No suggestions" not in fb for fb in all_feedback)
if issues_found:
    checklist = """
- [x] Automated review executed
- [x] :x: Issues found
- [ ] :white_check_mark: No issues found
"""
else:
    checklist = """
- [x] Automated review executed
- [ ] :x: Issues found
- [x] :white_check_mark: No issues found
"""
comment_body = f"""
## :robot_face: Watsonx AI Code Review
The following checklist summarizes the AI review:
{checklist}
---
{os.linesep.join(all_feedback)}
---
:white_check_mark: **Recommendation**: Please review the suggestions above and update the code accordingly.
"""
post_comment(PR_NUMBER, comment_body)


