import sys
import requests
from langchain_ibm import WatsonxLLM

# -------------------
# Config
# -------------------
GITHUB_TOKEN = "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".strip()
REPO = "PR-review"
ORG = "shilpadas2511-cmd"

# Your Watsonx credentials
WATSONX_APIKEY = "0naEz5glYS4UMnH6O_UCaGoCXUjTub00V1LdajsuKOzG"
WATSONX_PROJECT_ID = "6729461b-223c-4769-a525-821865221429"
WATSONX_URL = "https://us-south.ml.cloud.ibm.com"
MODEL_ID = "ibm/granite-3-8b-instruct"   # good balance for code review

if len(sys.argv) < 2:
    print("Usage: python pr_review_agent_watsonx.py <PR_NUMBER>")
    sys.exit(1)

PR_NUMBER = sys.argv[1]

# -------------------
# GitHub API to fetch changed files
# -------------------
pr_files_url = f"https://api.github.com/repos/{ORG}/{REPO}/pulls/{PR_NUMBER}/files"
headers = {
    "Authorization": GITHUB_TOKEN,   # ✅ FIXED (no "token " prefix)
    "Accept": "application/vnd.github.v3+json"
}

files_data = requests.get(pr_files_url, headers=headers).json()
if "message" in files_data:
    print(f"❌ GitHub API Error: {files_data['message']}")
    sys.exit(1)

# Collect only Java files
java_files = []
for file in files_data:
    filename = file.get("filename")
    patch = file.get("patch")

    if filename and patch and filename.endswith(".java"):
        # fetch full file content for better review
        file_url = f"https://api.github.com/repos/{ORG}/{REPO}/contents/{filename}"
        file_resp = requests.get(file_url, headers=headers).json()
        if "content" in file_resp:
            import base64
            full_code = base64.b64decode(file_resp["content"]).decode("utf-8")
        else:
            full_code = patch  # fallback to patch only

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
        feedback = llm.invoke(prompt)
    except Exception as e:
        feedback = f"❌ Watsonx call failed: {str(e)}"

    results[filename] = feedback

# -------------------
# Output
# -------------------
print("\n" + "="*80)
print("🤖 Watsonx AI Best-Practice Suggestions (Java Files Only)")
print("="*80)
for filename, suggestions in results.items():
    print(f"\nFile: {filename}")
    print(suggestions)
