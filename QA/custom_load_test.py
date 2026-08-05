import time
import requests
import concurrent.futures
import statistics
import pandas as pd
import os
from datetime import datetime

# Configuration
target_url = "https://suresh595817.github.io/MuscleIq/"
TOTAL_TESTS = 300

print(f"Starting Load Test with {TOTAL_TESTS} total requests against {target_url}...")

# Metrics
results = []
start_time = time.time()

endpoints = ["/", "/tracker", "/ai"]

def make_request(request_id):
    endpoint = endpoints[request_id % len(endpoints)]
    full_url = target_url + endpoint
    
    req_start = time.time()
    try:
        response = requests.get(full_url, timeout=10)
        req_time = time.time() - req_start
        status_code = response.status_code
        status = "PASS" if status_code == 200 else "FAIL"
    except requests.RequestException:
        req_time = time.time() - req_start
        status_code = "N/A"
        status = "ERROR"

    return {
        "Test ID": f"LOAD_TC_{request_id+1:03d}",
        "Module": "Performance",
        "Test Name": f"Load Test - {endpoint}",
        "Priority": "MEDIUM",
        "Preconditions": "Target online",
        "Steps": f"HTTP GET {full_url}",
        "Test Data": "N/A",
        "Expected Result": "Status 200 OK within 10s",
        "Actual Result": f"Status Code: {status_code}",
        "Status": status,
        "Duration (ms)": int(req_time * 1000),
        "Device": "Virtual User"
    }

# Run the test using a thread pool to simulate concurrent load
with concurrent.futures.ThreadPoolExecutor(max_workers=20) as executor:
    futures = [executor.submit(make_request, i) for i in range(TOTAL_TESTS)]
    
    for i, future in enumerate(concurrent.futures.as_completed(futures)):
        results.append(future.result())
        if (i + 1) % 50 == 0:
            print(f"Executed {i + 1}/{TOTAL_TESTS} load tests...")

# Sort results by Test ID to maintain order
results = sorted(results, key=lambda x: x["Test ID"])

# Calculate aggregate results
total_time = time.time() - start_time
rps = TOTAL_TESTS / total_time
valid_times = [r["Response Time (s)"] for r in results if r["Status Code"] != "N/A"]
avg_time = statistics.mean(valid_times) if valid_times else 0
max_time = max(valid_times) if valid_times else 0
min_time = min(valid_times) if valid_times else 0

# Create Reports Directory
os.makedirs("reports", exist_ok=True)

# Generate Enterprise Excel Report
try:
    import sys
    sys.path.append(os.path.dirname(__file__))
    from excel_generator import generate_enterprise_report
    excel_path = "reports/load_report.xlsx"
    generate_enterprise_report(results, excel_path, "Load & Performance")
except Exception as e:
    print(f"Failed to generate Enterprise Excel report: {e}")
    # Fallback to pandas
    df = pd.DataFrame(results)
    excel_path = "reports/load_report.xlsx"
    df.to_excel(excel_path, index=False)

# Generate HTML Report
html_path = "reports/load_report.html"
html_content = f"""
<html>
<head>
    <title>Load & Performance Testing Report</title>
    <style>
        body {{ font-family: Arial, sans-serif; margin: 20px; }}
        table {{ border-collapse: collapse; width: 100%; }}
        th, td {{ border: 1px solid #ddd; padding: 8px; text-align: left; }}
        th {{ background-color: #2196F3; color: white; }}
        .summary {{ background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin-bottom: 20px; }}
        .pass {{ color: green; font-weight: bold; }}
        .fail {{ color: red; font-weight: bold; }}
    </style>
</head>
<body>
    <h1>Load & Performance Testing Report</h1>
    
    <div class="summary">
        <h2>Executive Summary</h2>
        <p><strong>Target URL:</strong> {target_url}</p>
        <p><strong>Total Requests Executed:</strong> {TOTAL_TESTS}</p>
        <p><strong>Total Duration:</strong> {total_time:.2f} seconds</p>
        <p><strong>Requests Per Second (RPS):</strong> {rps:.2f} req/sec</p>
        <p><strong>Average Response Time:</strong> {avg_time:.3f} s</p>
        <p><strong>Max Response Time:</strong> {max_time:.3f} s</p>
        <p><strong>Date:</strong> {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}</p>
    </div>

    <table>
        <tr>
            <th>Test ID</th>
            <th>Endpoint</th>
            <th>Status Code</th>
            <th>Response Time (s)</th>
            <th>Result</th>
        </tr>
"""
for r in results:
    css_class = "pass" if "PASS" in r["Result"] else "fail"
    html_content += f"""
        <tr>
            <td>{r['Test ID']}</td>
            <td>{r['Endpoint']}</td>
            <td>{r['Status Code']}</td>
            <td>{r['Response Time (s)']}</td>
            <td class="{css_class}">{r['Result']}</td>
        </tr>
    """
html_content += """
    </table>
</body>
</html>
"""

with open(html_path, "w", encoding="utf-8") as f:
    f.write(html_content)

print(f"Load test complete! Reports generated at {excel_path} and {html_path}")
