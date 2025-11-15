# Troubleshooting Guide - Purchase Payments Shop Loading

## Issue: Shop Names Not Loading in Purchase Payment Management

### Quick Fix Checklist

1. **Rebuild the Application**
   ```cmd
   mvn clean package -DskipTests
   ```

2. **Restart the Application**
   ```cmd
   java -jar target\shree-ranga-traders-1.0.0.jar
   ```

3. **Clear Browser Cache**
   - Press `Ctrl + Shift + Delete`
   - Select "Cached images and files"
   - Clear cache
   - Refresh the page (`Ctrl + F5`)

### Verification Steps

#### Step 1: Check Backend API
Open browser and navigate to:
```
http://localhost:8080/api/shops
```

**Expected Result**: Should return JSON array of shops
```json
[
  {
    "id": 1,
    "shopName": "ABC Store",
    "contact": "1234567890",
    "address": "123 Main St",
    "createdDate": "2025-11-15T10:00:00",
    "updatedDate": "2025-11-15T10:00:00"
  }
]
```

**If empty array `[]`**: You need to add shops via the Master Data page first.

**If error**: Check application logs for backend issues.

#### Step 2: Check Browser Console
1. Open Purchase Payments page: `http://localhost:8080/purchase-payments`
2. Press `F12` to open Developer Tools
3. Go to **Console** tab
4. Look for errors

**Common Issues**:
- ❌ `Failed to fetch` → Backend not running
- ❌ `404 Not Found` → URL endpoint mismatch
- ❌ `CORS error` → CORS configuration issue
- ✅ No errors → Check Network tab

#### Step 3: Check Network Tab
1. In Developer Tools, go to **Network** tab
2. Refresh the page (`F5`)
3. Look for request to `/api/shops`
4. Check the response

**What to verify**:
- Status: Should be `200 OK`
- Response: Should contain shop data
- Type: Should be `application/json`

### Common Problems and Solutions

#### Problem 1: Empty Shop List
**Symptom**: Dropdown shows only "Select Shop" / "All Shops" with no shops listed

**Solution**:
1. Go to Master Data page (`/master-data`)
2. Add shops using the "Shops" section
3. Refresh Purchase Payments page

#### Problem 2: Old Version Loading
**Symptom**: Changes not reflected after rebuild

**Solution**:
1. Stop the application completely
2. Delete `target` folder
   ```cmd
   rmdir /s /q target
   ```
3. Rebuild
   ```cmd
   mvn clean package -DskipTests
   ```
4. Start application
   ```cmd
   java -jar target\shree-ranga-traders-1.0.0.jar
   ```
5. Clear browser cache (Ctrl + Shift + Delete)
6. Hard refresh page (Ctrl + F5)

#### Problem 3: JavaScript Not Loading
**Symptom**: Dropdowns never populate, no console errors

**Solution**:
1. Check if JavaScript is enabled in browser
2. Check if any browser extensions are blocking scripts
3. Try in Incognito/Private mode
4. Check browser's Developer Tools → Sources tab to see if purchase-payments.html loaded

#### Problem 4: Database Connection Issues
**Symptom**: 500 Internal Server Error when accessing `/api/shops`

**Solution**:
1. Check `application.properties` database configuration
2. Verify database is running
3. Check application logs for database connection errors
4. Verify `shop` table exists in database

### Testing the Fix

#### Test 1: Payment Form Dropdown
1. Navigate to Purchase Payments page
2. Look at "Record Payment" section
3. Click on "Shop Name" dropdown
4. **Expected**: Shows "Select Shop" + list of all shops

#### Test 2: Search Form Dropdown
1. Navigate to Purchase Payments page
2. Look at "Search Payment History" section
3. Click on "Shop Name" dropdown under search
4. **Expected**: Shows "All Shops" + list of all shops

#### Test 3: Type Filter Dropdown
1. Navigate to Purchase Payments page
2. Look at "Search Payment History" section
3. Click on "Type" dropdown
4. **Expected**: Shows "All Types", "PURCHASE", "PAYMENT", "ADJUSTMENT"

### Code Verification

If you want to manually verify the code is correct, check:

**File**: `src/main/resources/templates/purchase-payments.html`

**Line ~233-250**: Should contain:
```javascript
async function loadShops() {
    try {
        const response = await fetch('/api/shops');
        const shops = await response.json();
        const shopSelect = document.getElementById('shopName');
        const searchShopSelect = document.getElementById('searchShopName');

        shopSelect.innerHTML = '<option value="">Select Shop</option>';
        searchShopSelect.innerHTML = '<option value="">All Shops</option>';

        shops.forEach(shop => {
            shopSelect.innerHTML += `<option value="${shop.shopName}">${shop.shopName}</option>`;
            searchShopSelect.innerHTML += `<option value="${shop.shopName}">${shop.shopName}</option>`;
        });
    } catch (error) {
        console.error('Error loading shops:', error);
    }
}
```

**Line ~227**: Should contain:
```javascript
loadShops();
```

### Still Having Issues?

If shops still don't load after following all steps:

1. **Check Application Logs**
   - Look for any error messages when application starts
   - Check for errors when accessing `/api/shops`

2. **Verify Database Schema**
   ```sql
   SELECT * FROM shop;
   ```
   Make sure the table exists and has data.

3. **Test API Directly**
   Use a tool like Postman or curl:
   ```cmd
   curl http://localhost:8080/api/shops
   ```

4. **Check Browser Compatibility**
   - Modern browsers required (Chrome 90+, Firefox 88+, Edge 90+)
   - JavaScript must be enabled

5. **Review Application Configuration**
   - Check `application.properties` for correct port (default: 8080)
   - Ensure no firewall blocking access
   - Verify application started successfully

## Summary

The shop loading functionality is correctly implemented. The most common solution is:
1. **Rebuild** the application: `mvn clean package -DskipTests`
2. **Restart** the application
3. **Clear browser cache** and hard refresh (Ctrl + F5)

The dropdowns should now show:
- ✅ Payment Form: "Select Shop" + all shops
- ✅ Search Form: "All Shops" + all shops  
- ✅ Type Filter: "All Types" + transaction types

