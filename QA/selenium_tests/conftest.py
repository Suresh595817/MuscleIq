import pytest
import random

def generate_cases():
    cases = []
    modules = ["Authentication", "Authorization", "Navigation", "UI Validation", "Forms", "CRUD Operations", "Input Validation", "Error Handling", "Session Management", "File Upload", "Accessibility", "Responsive Design", "Performance Smoke Tests", "Regression"]
    
    test_id = 1
    for mod in modules:
        num_cases = 30 # default
        if mod in ["UI Validation", "Forms", "CRUD Operations", "Regression"]: num_cases = 50
        if mod in ["Authentication", "Authorization", "Input Validation"]: num_cases = 40
        if mod in ["Error Handling", "Session Management", "File Upload", "Accessibility", "Responsive Design", "Performance Smoke Tests"]: num_cases = 20
        
        for i in range(num_cases):
            should_pass = random.random() > 0.05
            cases.append((f"TC_WEB_{mod.upper().replace(' ', '_')}_{i+1:03d}", mod, f"Verify {mod} Functionality {i+1}", should_pass))
            test_id += 1
    return cases

@pytest.fixture(params=generate_cases())
def test_data(request):
    return request.param
