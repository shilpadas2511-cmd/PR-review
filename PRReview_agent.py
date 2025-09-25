import os
import sys
import requests
import base64
from openai import OpenAI
# -------------------
# Config
# -------------------
GITHUB_TOKEN = "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".strip()
REPO = "PR-review"
ORG = "shilpadas2511-cmd"
# OpenAI key (⚠️ for quick test only, better use env variable later)
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=OPENAI_API_KEY)
MODEL_ID = "gpt-4o-mini"   # small + cheap, good for code review
if len(sys.argv) < 2:
    print("Usage: python pr_review_agent_openai.py <PR_NUMBER>")
    sys.exit(1)
PR_NUMBER = sys.argv[1]
# -------------------
# GitHub API to fetch changed files
# -------------------
pr_files_url = f"https://api.github.com/repos/{ORG}/{REPO}/pulls/{PR_NUMBER}/files"
headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
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
        # fetch full file content
        file_url = f"https://api.github.com/repos/{ORG}/{REPO}/contents/{filename}"
        file_resp = requests.get(file_url, headers=headers).json()
        if "content" in file_resp:
            full_code = base64.b64decode(file_resp["content"]).decode("utf-8")
        else:
            full_code = patch  # fallback to patch only
        java_files.append({"filename": filename, "code": full_code})
if not java_files:
    print("No Java files changed in this PR.")
    sys.exit(0)
# -------------------
# Loop through changed Java files
# -------------------
results = {}
for f in java_files:
    filename = f["filename"]
    code = f["code"]
    # truncate if extremely long
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
        response = client.chat.completions.create(
            model=MODEL_ID,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.3,
        )
        feedback = response.choices[0].message.content
    except Exception as e:
        feedback = f":x: OpenAI call failed: {str(e)}"
    results[filename] = feedback
# -------------------
# Output
# -------------------
print("\n" + "="*80)
print(":robot_face: OpenAI Best-Practice Suggestions (Java Files Only)")
print("="*80)
for filename, suggestions in results.items():
    print(f"\nFile: {filename}")
    print(suggestions)












