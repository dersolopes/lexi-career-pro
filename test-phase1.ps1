# Phase 1 Test Script for LexiCareer Pro
# This script executes all Phase 1 API tests programmatically

$BASE_URL = "http://localhost:8080"
$JWT_TOKEN = ""
$TEST_RESULTS = @()

function Write-TestResult {
    param(
        [string]$TestName,
        [bool]$Passed,
        [string]$Details = ""
    )
    
    $status = if ($Passed) { "PASS" } else { "FAIL" }
    $color = if ($Passed) { "Green" } else { "Red" }
    
    Write-Host "[$status] $TestName" -ForegroundColor $color
    if ($Details) {
        Write-Host "    $Details" -ForegroundColor Gray
    }
    
    $TEST_RESULTS += @{
        Test = $TestName
        Passed = $Passed
        Details = $Details
    }
}

function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Endpoint,
        [hashtable]$Headers = @{},
        [string]$Body = $null
    )
    
    $url = "$BASE_URL$Endpoint"
    $headers["Content-Type"] = "application/json"
    
    if ($JWT_TOKEN -and -not $Headers.ContainsKey("Authorization")) {
        $Headers["Authorization"] = "Bearer $JWT_TOKEN"
    }
    
    try {
        if ($Body) {
            $response = Invoke-WebRequest -Uri $url -Method $Method -Headers $Headers -Body $Body -ErrorAction Stop
        } else {
            $response = Invoke-WebRequest -Uri $url -Method $Method -Headers $Headers -ErrorAction Stop
        }
        return @{
            Success = $true
            StatusCode = $response.StatusCode
            Content = $response.Content
        }
    } catch {
        return @{
            Success = $false
            StatusCode = $_.Exception.Response.StatusCode.value__
            Content = $_.Exception.Message
        }
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Phase 1 Tests - LexiCareer Pro" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. Authentication Tests
Write-Host "1. AUTHENTICATION TESTS" -ForegroundColor Yellow

# 1.1 Register User
Write-Host "`n1.1 Register User" -ForegroundColor Magenta
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testEmail = "testuser$timestamp@example.com"
$registerBody = @{
    email = $testEmail
    password = "Password123!"
    name = "Test User"
} | ConvertTo-Json

$registerResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/auth/register" -Body $registerBody
if ($registerResult.Success -and $registerResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Register User" -Passed $true -Details "User registered successfully with email: $testEmail"
    $registerContent = $registerResult.Content | ConvertFrom-Json
    $JWT_TOKEN = $registerContent.token
} else {
    Write-TestResult -TestName "Register User" -Passed $false -Details "Failed: $($registerResult.StatusCode) - $($registerResult.Content)"
}

# 1.2 Login
Write-Host "`n1.2 Login" -ForegroundColor Magenta
$loginBody = @{
    email = $testEmail
    password = "Password123!"
} | ConvertTo-Json

$loginResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/auth/login" -Body $loginBody
if ($loginResult.Success -and $loginResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Login" -Passed $true -Details "Login successful"
    $loginContent = $loginResult.Content | ConvertFrom-Json
    $JWT_TOKEN = $loginContent.token
} else {
    Write-TestResult -TestName "Login" -Passed $false -Details "Failed: $($loginResult.StatusCode) - $($loginResult.Content)"
}

# 1.3 Login with wrong password
Write-Host "`n1.3 Login with wrong password" -ForegroundColor Magenta
$wrongPasswordBody = @{
    email = $testEmail
    password = "WrongPassword123!"
} | ConvertTo-Json

$wrongLoginResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/auth/login" -Body $wrongPasswordBody
if (-not $wrongLoginResult.Success -and $wrongLoginResult.StatusCode -eq 401) {
    Write-TestResult -TestName "Login with wrong password" -Passed $true -Details "Correctly returned 401 Unauthorized"
} else {
    Write-TestResult -TestName "Login with wrong password" -Passed $false -Details "Expected 401, got: $($wrongLoginResult.StatusCode)"
}

# 2. User Profile Tests
Write-Host "`n2. USER PROFILE TESTS" -ForegroundColor Yellow

# 2.1 Get Current User
Write-Host "`n2.1 Get Current User" -ForegroundColor Magenta
$getUserResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/user/me"
if ($getUserResult.Success -and $getUserResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get Current User" -Passed $true -Details "User data retrieved successfully"
} else {
    Write-TestResult -TestName "Get Current User" -Passed $false -Details "Failed: $($getUserResult.StatusCode) - $($getUserResult.Content)"
}

# 2.2 Update Profession
Write-Host "`n2.2 Update Profession" -ForegroundColor Magenta
$professionBody = @{
    profession = "Software Developer"
} | ConvertTo-Json

$updateProfessionResult = Invoke-ApiRequest -Method "PUT" -Endpoint "/api/user/profession" -Body $professionBody
if ($updateProfessionResult.Success -and $updateProfessionResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Update Profession" -Passed $true -Details "Profession updated successfully"
} else {
    Write-TestResult -TestName "Update Profession" -Passed $false -Details "Failed: $($updateProfessionResult.StatusCode) - $($updateProfessionResult.Content)"
}

# 2.3 Update Level
Write-Host "`n2.3 Update Level" -ForegroundColor Magenta
$levelBody = @{
    level = "INTERMEDIATE"
} | ConvertTo-Json

$updateLevelResult = Invoke-ApiRequest -Method "PUT" -Endpoint "/api/user/level" -Body $levelBody
if ($updateLevelResult.Success -and $updateLevelResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Update Level" -Passed $true -Details "Level updated successfully"
} else {
    Write-TestResult -TestName "Update Level" -Passed $false -Details "Failed: $($updateLevelResult.StatusCode) - $($updateLevelResult.Content)"
}

# 3. Categories Tests
Write-Host "`n3. CATEGORIES TESTS" -ForegroundColor Yellow

# 3.1 Create Category
Write-Host "`n3.1 Create Category" -ForegroundColor Magenta
$categoryBody = @{
    name = "Software Development"
    description = "Terms related to software development"
    planType = "FREE"
    displayOrder = 1
    slug = "software-development"
} | ConvertTo-Json

$createCategoryResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/categories" -Body $categoryBody
if ($createCategoryResult.Success -and $createCategoryResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Create Category" -Passed $true -Details "Category created successfully"
    $categoryContent = $createCategoryResult.Content | ConvertFrom-Json
    $CATEGORY_ID = $categoryContent.id
} else {
    Write-TestResult -TestName "Create Category" -Passed $false -Details "Failed: $($createCategoryResult.StatusCode) - $($createCategoryResult.Content)"
    $CATEGORY_ID = 1
}

# 3.2 List Categories
Write-Host "`n3.2 List Categories" -ForegroundColor Magenta
$listCategoriesResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/categories"
if ($listCategoriesResult.Success -and $listCategoriesResult.StatusCode -eq 200) {
    Write-TestResult -TestName "List Categories" -Passed $true -Details "Categories retrieved successfully"
} else {
    Write-TestResult -TestName "List Categories" -Passed $false -Details "Failed: $($listCategoriesResult.StatusCode) - $($listCategoriesResult.Content)"
}

# 3.3 Get Category by ID
Write-Host "`n3.3 Get Category by ID" -ForegroundColor Magenta
$getCategoryResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/categories/$CATEGORY_ID"
if ($getCategoryResult.Success -and $getCategoryResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get Category by ID" -Passed $true -Details "Category retrieved successfully"
} else {
    Write-TestResult -TestName "Get Category by ID" -Passed $false -Details "Failed: $($getCategoryResult.StatusCode) - $($getCategoryResult.Content)"
}

# 3.4 Update Category
Write-Host "`n3.4 Update Category" -ForegroundColor Magenta
$updateCategoryBody = @{
    name = "Software Development"
    description = "Updated description for software development"
    planType = "FREE"
    displayOrder = 1
    slug = "software-development"
} | ConvertTo-Json

$updateCategoryResult = Invoke-ApiRequest -Method "PUT" -Endpoint "/api/categories/$CATEGORY_ID" -Body $updateCategoryBody
if ($updateCategoryResult.Success -and $updateCategoryResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Update Category" -Passed $true -Details "Category updated successfully"
} else {
    Write-TestResult -TestName "Update Category" -Passed $false -Details "Failed: $($updateCategoryResult.StatusCode) - $($updateCategoryResult.Content)"
}

# 4. Terms Tests
Write-Host "`n4. TERMS TESTS" -ForegroundColor Yellow

# 4.1 Create Term
Write-Host "`n4.1 Create Term" -ForegroundColor Magenta
$termBody = @{
    name = "API"
    definition = "Application Programming Interface - a set of rules for building software"
    simpleExplanation = "An interface that allows different systems to communicate"
    example = "Facebook uses APIs to allow third-party apps to access user data"
    difficultyLevel = "BEGINNER"
    categoryId = $CATEGORY_ID
    planType = "FREE"
} | ConvertTo-Json

$createTermResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/terms" -Body $termBody
if ($createTermResult.Success -and $createTermResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Create Term" -Passed $true -Details "Term created successfully"
    $termContent = $createTermResult.Content | ConvertFrom-Json
    $TERM_ID = $termContent.id
} else {
    Write-TestResult -TestName "Create Term" -Passed $false -Details "Failed: $($createTermResult.StatusCode) - $($createTermResult.Content)"
    $TERM_ID = 1
}

# 4.2 Get Term by ID
Write-Host "`n4.2 Get Term by ID" -ForegroundColor Magenta
$getTermResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/terms/$TERM_ID"
if ($getTermResult.Success -and $getTermResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get Term by ID" -Passed $true -Details "Term retrieved successfully"
} else {
    Write-TestResult -TestName "Get Term by ID" -Passed $false -Details "Failed: $($getTermResult.StatusCode) - $($getTermResult.Content)"
}

# 4.3 Search Terms
Write-Host "`n4.3 Search Terms" -ForegroundColor Magenta
$searchTermsResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/terms/search?keyword=api"
if ($searchTermsResult.Success -and $searchTermsResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Search Terms" -Passed $true -Details "Terms searched successfully"
} else {
    Write-TestResult -TestName "Search Terms" -Passed $false -Details "Failed: $($searchTermsResult.StatusCode) - $($searchTermsResult.Content)"
}

# 4.4 Get Daily Term
Write-Host "`n4.4 Get Daily Term" -ForegroundColor Magenta
$dailyTermResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/terms/daily"
if ($dailyTermResult.Success -and $dailyTermResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get Daily Term" -Passed $true -Details "Daily term retrieved successfully"
} else {
    Write-TestResult -TestName "Get Daily Term" -Passed $false -Details "Failed: $($dailyTermResult.StatusCode) - $($dailyTermResult.Content)"
}

# 4.5 Mark Term as Studied
Write-Host "`n4.5 Mark Term as Studied" -ForegroundColor Magenta
$markStudiedResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/terms/$TERM_ID/studied?knowledgeLevel=KNOWN"
if ($markStudiedResult.Success -and $markStudiedResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Mark Term as Studied" -Passed $true -Details "Term marked as studied successfully"
} else {
    Write-TestResult -TestName "Mark Term as Studied" -Passed $false -Details "Failed: $($markStudiedResult.StatusCode) - $($markStudiedResult.Content)"
}

# 4.6 Toggle Favorite
Write-Host "`n4.6 Toggle Favorite" -ForegroundColor Magenta
$toggleFavoriteResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/terms/$TERM_ID/favorite"
if ($toggleFavoriteResult.Success -and $toggleFavoriteResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Toggle Favorite" -Passed $true -Details "Term favorited successfully"
} else {
    Write-TestResult -TestName "Toggle Favorite" -Passed $false -Details "Failed: $($toggleFavoriteResult.StatusCode) - $($toggleFavoriteResult.Content)"
}

# 4.7 Check if Favorite
Write-Host "`n4.7 Check if Favorite" -ForegroundColor Magenta
$checkFavoriteResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/terms/$TERM_ID/favorite"
if ($checkFavoriteResult.Success -and $checkFavoriteResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Check if Favorite" -Passed $true -Details "Favorite status checked successfully"
} else {
    Write-TestResult -TestName "Check if Favorite" -Passed $false -Details "Failed: $($checkFavoriteResult.StatusCode) - $($checkFavoriteResult.Content)"
}

# 4.8 Get Favorite Terms
Write-Host "`n4.8 Get Favorite Terms" -ForegroundColor Magenta
$getFavoritesResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/terms/favorites"
if ($getFavoritesResult.Success -and $getFavoritesResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get Favorite Terms" -Passed $true -Details "Favorite terms retrieved successfully"
} else {
    Write-TestResult -TestName "Get Favorite Terms" -Passed $false -Details "Failed: $($getFavoritesResult.StatusCode) - $($getFavoritesResult.Content)"
}

# 5. Dashboard Test
Write-Host "`n5. DASHBOARD TEST" -ForegroundColor Yellow

# 5.1 Get Dashboard
Write-Host "`n5.1 Get Dashboard" -ForegroundColor Magenta
$dashboardResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/dashboard"
if ($dashboardResult.Success -and $dashboardResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get Dashboard" -Passed $true -Details "Dashboard retrieved successfully"
} else {
    Write-TestResult -TestName "Get Dashboard" -Passed $false -Details "Failed: $($dashboardResult.StatusCode) - $($dashboardResult.Content)"
}

# 6. User Interests Tests
Write-Host "`n6. USER INTERESTS TESTS" -ForegroundColor Yellow

# 6.1 Add Interest
Write-Host "`n6.1 Add Interest" -ForegroundColor Magenta
$interestBody = @{
    categoryId = $CATEGORY_ID
} | ConvertTo-Json

$addInterestResult = Invoke-ApiRequest -Method "POST" -Endpoint "/api/user/interests" -Body $interestBody
if ($addInterestResult.Success -and $addInterestResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Add Interest" -Passed $true -Details "Interest added successfully"
} else {
    Write-TestResult -TestName "Add Interest" -Passed $false -Details "Failed: $($addInterestResult.StatusCode) - $($addInterestResult.Content)"
}

# 6.2 Get User Interests
Write-Host "`n6.2 Get User Interests" -ForegroundColor Magenta
$getInterestsResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/user/interests"
if ($getInterestsResult.Success -and $getInterestsResult.StatusCode -eq 200) {
    Write-TestResult -TestName "Get User Interests" -Passed $true -Details "User interests retrieved successfully"
} else {
    Write-TestResult -TestName "Get User Interests" -Passed $false -Details "Failed: $($getInterestsResult.StatusCode) - $($getInterestsResult.Content)"
}

# 7. Error Handling Tests
Write-Host "`n7. ERROR HANDLING TESTS" -ForegroundColor Yellow

# 7.1 Access without token
Write-Host "`n7.1 Access without token" -ForegroundColor Magenta
$tempToken = $JWT_TOKEN
$JWT_TOKEN = ""
$noTokenResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/user/me"
$JWT_TOKEN = $tempToken

if (-not $noTokenResult.Success -and $noTokenResult.StatusCode -eq 401) {
    Write-TestResult -TestName "Access without token" -Passed $true -Details "Correctly returned 401 Unauthorized"
} else {
    Write-TestResult -TestName "Access without token" -Passed $false -Details "Expected 401, got: $($noTokenResult.StatusCode)"
}

# 7.2 Access non-existent resource
Write-Host "`n7.2 Access non-existent resource" -ForegroundColor Magenta
$notFoundResult = Invoke-ApiRequest -Method "GET" -Endpoint "/api/categories/99999"
if (-not $notFoundResult.Success -and $notFoundResult.StatusCode -eq 404) {
    Write-TestResult -TestName "Access non-existent resource" -Passed $true -Details "Correctly returned 404 Not Found"
} else {
    Write-TestResult -TestName "Access non-existent resource" -Passed $false -Details "Expected 404, got: $($notFoundResult.StatusCode)"
}

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$passedCount = ($TEST_RESULTS | Where-Object { $_.Passed -eq $true }).Count
$failedCount = ($TEST_RESULTS | Where-Object { $_.Passed -eq $false }).Count
$totalCount = $TEST_RESULTS.Count

Write-Host "Total Tests: $totalCount" -ForegroundColor White
Write-Host "Passed: $passedCount" -ForegroundColor Green
Write-Host "Failed: $failedCount" -ForegroundColor Red

if ($failedCount -gt 0) {
    Write-Host "`nFailed Tests:" -ForegroundColor Red
    $TEST_RESULTS | Where-Object { $_.Passed -eq $false } | ForEach-Object {
        Write-Host "  - $($_.Test): $($_.Details)" -ForegroundColor Red
    }
}

Write-Host "`n========================================`n" -ForegroundColor Cyan
