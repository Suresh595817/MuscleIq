import time
import requests
import concurrent.futures
import statistics

# Configuration
target_url = "http://localhost:5173"
duration = 60 # seconds
concurrent_users = 100

print(f"Starting Load Test on {target_url} for {duration} seconds with {concurrent_users} virtual users...")

# Metrics
response_times = []
start_time = time.time()
request_count = 0

def simulate_user():
    global request_count
    # A single user session hitting multiple pages
    endpoints = ["/", "/track", "/analytics"]
    
    while time.time() - start_time < duration:
        for endpoint in endpoints:
            if time.time() - start_time >= duration:
                break
                
            req_start = time.time()
            try:
                response = requests.get(target_url + endpoint, timeout=5)
                req_time = (time.time() - req_start) * 1000 # to ms
                response_times.append(req_time)
                request_count += 1
            except requests.RequestException:
                pass # ignore failed requests for baseline

# Run the test
with concurrent.futures.ThreadPoolExecutor(max_workers=concurrent_users) as executor:
    futures = [executor.submit(simulate_user) for _ in range(concurrent_users)]
    concurrent.futures.wait(futures)

# Calculate results
total_time = time.time() - start_time
rps = request_count / total_time

if response_times:
    avg_time = statistics.mean(response_times)
    min_time = min(response_times)
    max_time = max(response_times)
else:
    avg_time = min_time = max_time = 0

# Generate Markdown Report
report = f"""# Baseline Load Test Report

**Target:** {target_url}
**Duration:** {total_time:.2f} seconds
**Virtual Users:** {concurrent_users}

## Requests per second (RPS)
**{rps:.2f} req/sec**
Meaning your application is handling about {int(rps)} requests every second.

## Response Time
- **Average:** {avg_time:.2f}ms
- **Min:** {min_time:.2f}ms
- **Max:** {max_time:.2f}ms

Meaning:
- Fastest response = {min_time:.2f}ms
- Average = {avg_time:.2f}ms
- Slowest = {max_time:.2f}ms
"""

with open("load_test_report.md", "w") as f:
    f.write(report)

print("Load test complete! Report generated at load_test_report.md")
