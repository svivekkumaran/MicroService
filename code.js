document.addEventListener('DOMContentLoaded', () => {
    // --- DOM Elements ---
    const navLinks = document.querySelectorAll('.nav-link');
    const contentSections = document.querySelectorAll('.content-section');
    const addProductBtn = document.getElementById('add-product-btn');
    const addProductModal = document.getElementById('add-product-modal');
    const addCustomerBtn = document.getElementById('add-customer-btn');
    const addCustomerQuickBtn = document.getElementById('add-customer-quick-btn');
    const addCustomerModal = document.getElementById('add-customer-modal');
    const closeModalBtns = document.querySelectorAll('.close-btn');
    const addProductForm = document.getElementById('add-product-form');
    const addCustomerForm = document.getElementById('add-customer-form');
    const productListTableBody = document.getElementById('product-list');
    const customerListTableBody = document.getElementById('customer-list');
    const productCategorySelect = document.getElementById('product-category');
    const settingCategoryList = document.getElementById('setting-category-list');
    const settingAddCategoryBtn = document.getElementById('setting-add-category-btn');
    const settingNewCategoryInput = document.getElementById('setting-new-category');
    const billingProductSearch = document.getElementById('billing-product-search');
    const billingSearchResults = document.getElementById('billing-search-results');
    const billingQuantityInput = document.getElementById('billing-quantity');
    const addToCartBtn = document.getElementById('add-to-cart-btn');
    const cartItemsTableBody = document.getElementById('cart-items');
    const cartSubtotalEl = document.getElementById('cart-subtotal');
    const cartTaxAmountEl = document.getElementById('cart-tax-amount');
    const cartGrandTotalEl = document.getElementById('cart-grand-total');
    const cartTaxRateEl = document.getElementById('cart-tax-rate');
    const generateInvoiceBtn = document.getElementById('generate-invoice-btn');
    const invoicePreviewModal = document.getElementById('invoice-preview-modal');
    const printInvoiceBtn = document.getElementById('print-invoice-btn');
    const sharePdfBtn = document.getElementById('share-pdf-btn');
    const salesHistoryListBody = document.getElementById('sales-history-list');
    const dashboardStockAlerts = document.getElementById('stock-alerts-list');
    const dashboardDailySales = document.getElementById('daily-sales');
    const dashboardMonthlyRevenue = document.getElementById('monthly-revenue');
    const dashboardRecentTransactions = document.getElementById('recent-transactions-list');
    const billingCustomerSelect = document.getElementById('billing-customer-select');
    const settingTaxRateInput = document.getElementById('setting-tax-rate');
    const saveSettingsBtn = document.getElementById('save-settings-btn');


    // --- Application State (In-Memory - Lost on Refresh) ---
    let products = [
        // Sample Data
        { id: 'CEM001', code: 'CEM001', name: 'ACC Cement', category: 'Cement', unit: 'Bag', price: 350, stock: 150, reorderLevel: 20 },
        { id: 'SAN001', code: 'SAN001', name: 'River Sand', category: 'Sand', unit: 'Ton', price: 1200, stock: 50, reorderLevel: 10 },
        { id: 'BRI001', code: 'BRI001', name: 'Red Bricks - Class A', category: 'Bricks', unit: 'Nos', price: 8, stock: 5000, reorderLevel: 1000 },
        { id: 'STE001', code: 'STE001', name: 'TMT Steel Bar 8mm', category: 'Steel', unit: 'KG', price: 65, stock: 500, reorderLevel: 50 },
    ];
    let customers = [
        { id: 1, name: 'Rajesh Kumar', phone: '9876543210', address: '123 Main St', gstin: '' },
        { id: 2, name: 'Builders Inc', phone: '8765432109', address: '456 Industrial Area', gstin: '29ABCDE1234F1Z5' }
    ];
    let salesHistory = [
        // Sample Data
        { invoiceNum: 'INV-0001', date: '2023-10-26', customerId: 1, customerName: 'Rajesh Kumar', items: [{ productId: 'CEM001', name: 'ACC Cement', qty: 10, price: 350 }], total: 3500, tax: 630, grandTotal: 4130 },
        { invoiceNum: 'INV-0002', date: '2023-10-25', customerId: 'walk-in', customerName: 'Walk-in', items: [{ productId: 'BRI001', name: 'Red Bricks - Class A', qty: 500, price: 8 }], total: 4000, tax: 480, grandTotal: 4480 } // GST @ 12% for bricks example
    ];
    let categories = ['Cement', 'Sand', 'Bricks', 'Steel', 'Aggregate', 'Plumbing', 'Electrical', 'Tools'];
    let cart = []; // { productId, name, qty, price, total }
    let nextProductId = products.length + 1;
    let nextCustomerId = customers.length + 1;
    let nextInvoiceNum = salesHistory.length + 1;
    let settings = {
        shopName: 'Karthik Agency',
        address: '123 Construction Lane, Buildsville',
        gstin: 'YOUR_GSTIN_HERE',
        contact: '080-12345678',
        defaultTaxRate: 18, // Default percentage
    };

    // --- Initialization ---
    function init() {
        loadSettings();
        renderProductTable();
        renderCategoryOptions();
        renderCategoryList();
        renderCustomerTable();
        renderCustomerOptions();
        updateDashboard();
        renderSalesHistoryTable();
        setupEventListeners();
        showSection('dashboard-section'); // Show dashboard initially
    }

    // --- Navigation ---
    function showSection(sectionId) {
        contentSections.forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(sectionId).classList.add('active');

        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.dataset.target === sectionId) {
                link.classList.add('active');
            }
        });
        // Re-render dynamic content if needed when switching sections
        if (sectionId === 'products-section') renderProductTable();
        if (sectionId === 'customers-section') renderCustomerTable();
        if (sectionId === 'billing-section') renderCustomerOptions(); // Update customer dropdown
        if (sectionId === 'history-section') renderSalesHistoryTable();
        if (sectionId === 'dashboard-section') updateDashboard();
    }

    // --- Modals ---
    function openModal(modalId) {
        document.getElementById(modalId).classList.add('active');
    }

    function closeModal(modalId) {
        document.getElementById(modalId).classList.remove('active');
         // Reset forms inside modal when closing
        const form = document.getElementById(modalId)?.querySelector('form');
        if (form) form.reset();
    }

    // --- Settings ---
    function loadSettings() {
        // In real app, load from localStorage or backend
        document.getElementById('setting-shop-name').value = settings.shopName;
        document.getElementById('setting-shop-address').value = settings.address;
        document.getElementById('setting-shop-gstin').value = settings.gstin;
        document.getElementById('setting-shop-contact').value = settings.contact;
        settingTaxRateInput.value = settings.defaultTaxRate;
        cartTaxRateEl.textContent = settings.defaultTaxRate; // Update tax rate display in cart
    }

    function saveSettings() {
        settings.shopName = document.getElementById('setting-shop-name').value;
        settings.address = document.getElementById('setting-shop-address').value;
        settings.gstin = document.getElementById('setting-shop-gstin').value;
        settings.contact = document.getElementById('setting-shop-contact').value;
        settings.defaultTaxRate = parseFloat(settingTaxRateInput.value) || 0;
        // In real app, save to localStorage or backend
        cartTaxRateEl.textContent = settings.defaultTaxRate;
        alert('Settings Saved!');
        updateCartTotals(); // Recalculate cart if tax rate changed
    }

    // --- Categories ---
    function renderCategoryOptions() {
        productCategorySelect.innerHTML = '<option value="">Select Category</option>'; // Clear existing
        categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat;
            option.textContent = cat;
            productCategorySelect.appendChild(option);
        });
    }
    function renderCategoryList() {
         settingCategoryList.innerHTML = ''; // Clear existing
         categories.forEach(cat => {
            const li = document.createElement('li');
            li.textContent = cat;
            // Add a delete button maybe?
            settingCategoryList.appendChild(li);
         });
    }
    function addCategory() {
        const newCategory = settingNewCategoryInput.value.trim();
        if (newCategory && !categories.includes(newCategory)) {
            categories.push(newCategory);
            renderCategoryOptions();
            renderCategoryList();
            settingNewCategoryInput.value = '';
        } else if (!newCategory) {
             alert('Please enter a category name.');
        } else {
            alert('Category already exists.');
        }
    }


    // --- Products ---
    function renderProductTable() {
        productListTableBody.innerHTML = ''; // Clear existing rows
        if (products.length === 0) {
            productListTableBody.innerHTML = '<tr><td colspan="6">No products found. Add some!</td></tr>';
            return;
        }
        products.forEach(p => {
            const row = productListTableBody.insertRow();
            row.innerHTML = `
                <td>${p.code || 'N/A'}</td>
                <td>${p.name}</td>
                <td>${p.category}</td>
                <td>${p.price.toFixed(2)}</td>
                <td class="${p.stock <= p.reorderLevel ? 'stock-alert' : ''}">${p.stock} ${p.unit}</td>
                <td>
                    <button class="btn btn-small" onclick="editProduct('${p.id}')">Edit</button>
                    <button class="btn btn-small btn-danger" onclick="deleteProduct('${p.id}')">Delete</button>
                </td>
            `;
        });
         updateStockAlerts();
    }

    function handleAddProduct(event) {
        event.preventDefault();
        const code = document.getElementById('product-code').value.trim();
        const name = document.getElementById('product-name').value.trim();
        const category = document.getElementById('product-category').value;
        const unit = document.getElementById('product-unit').value.trim();
        const price = parseFloat(document.getElementById('product-price').value);
        const stock = parseInt(document.getElementById('product-stock').value);
        const reorderLevel = parseInt(document.getElementById('product-reorder-level').value);

        if (!name || !category || !unit || isNaN(price) || price <= 0 || isNaN(stock) || stock < 0 || isNaN(reorderLevel)) {
            alert('Please fill in all required fields correctly.');
            return;
        }

        const newProduct = {
            id: code || `PROD${nextProductId++}`, // Generate ID if code is missing
            code: code || `PROD${nextProductId -1}`,
            name,
            category,
            unit,
            price,
            stock,
            reorderLevel
        };
        products.push(newProduct);
        renderProductTable();
        closeModal('add-product-modal');
    }

    // Placeholder functions for edit/delete
    window.editProduct = (id) => {
        alert(`Edit functionality for product ID ${id} needs implementation.`);
        // 1. Find product by ID
        // 2. Populate an 'Edit Product' modal form
        // 3. Add submit handler for the edit form to update the product array
    };

    window.deleteProduct = (id) => {
        if (confirm('Are you sure you want to delete this product? This cannot be undone.')) {
            products = products.filter(p => p.id !== id);
            renderProductTable();
             // Check if this product is in the cart and remove it
             const cartIndex = cart.findIndex(item => item.productId === id);
             if(cartIndex > -1) {
                 cart.splice(cartIndex, 1);
                 renderCart();
             }
        }
    };

    // --- Customers ---
     function renderCustomerTable() {
        customerListTableBody.innerHTML = ''; // Clear existing rows
        if (customers.length === 0) {
            customerListTableBody.innerHTML = '<tr><td colspan="4">No customers found.</td></tr>';
            return;
        }
        customers.forEach(c => {
            const row = customerListTableBody.insertRow();
            row.innerHTML = `
                <td>CUST${c.id}</td>
                <td>${c.name}</td>
                <td>${c.phone}</td>
                <td>
                    <button class="btn btn-small" onclick="viewCustomerHistory(${c.id})">History</button>
                    <button class="btn btn-small" onclick="editCustomer(${c.id})">Edit</button>
                </td>
            `;
        });
    }
     function renderCustomerOptions() {
        billingCustomerSelect.innerHTML = '<option value="walk-in">Walk-in Customer</option>'; // Clear existing except walk-in
        customers.forEach(c => {
            const option = document.createElement('option');
            option.value = c.id;
            option.textContent = `${c.name} (${c.phone})`;
            billingCustomerSelect.appendChild(option);
        });
     }

     function handleAddCustomer(event) {
         event.preventDefault();
        const name = document.getElementById('customer-name').value.trim();
        const phone = document.getElementById('customer-phone').value.trim();
        const address = document.getElementById('customer-address').value.trim();
        const gstin = document.getElementById('customer-gstin').value.trim();

        if (!name || !phone) {
             alert('Please enter at least Name and Phone.');
             return;
        }

        const newCustomer = {
            id: nextCustomerId++,
            name,
            phone,
            address,
            gstin
        };
        customers.push(newCustomer);
        renderCustomerTable();
        renderCustomerOptions(); // Update dropdowns
        closeModal('add-customer-modal');
        billingCustomerSelect.value = newCustomer.id; // Select the newly added customer in billing
     }

    // Placeholder functions
    window.viewCustomerHistory = (id) => alert(`View History for customer ID ${id} needs implementation.`);
    window.editCustomer = (id) => alert(`Edit functionality for customer ID ${id} needs implementation.`);


    // --- Billing ---
    let selectedBillingProduct = null;

    function handleBillingProductSearch() {
        const searchTerm = billingProductSearch.value.toLowerCase().trim();
        billingSearchResults.innerHTML = '';
        selectedBillingProduct = null; // Reset selection

        if (searchTerm.length < 2) {
             billingSearchResults.style.display = 'none';
            return;
        }

        const matchingProducts = products.filter(p =>
            p.name.toLowerCase().includes(searchTerm) ||
            (p.code && p.code.toLowerCase().includes(searchTerm))
        ).slice(0, 10); // Limit results

        if (matchingProducts.length > 0) {
             billingSearchResults.style.display = 'block';
             matchingProducts.forEach(p => {
                const div = document.createElement('div');
                div.textContent = `${p.name} (${p.code || 'No Code'}) - Stock: ${p.stock}`;
                div.onclick = () => selectBillingProduct(p);
                billingSearchResults.appendChild(div);
            });
        } else {
            billingSearchResults.style.display = 'none';
        }
    }

    function selectBillingProduct(product) {
        selectedBillingProduct = product;
        billingProductSearch.value = `${product.name} (${product.code || 'No Code'})`;
        billingSearchResults.innerHTML = '';
        billingSearchResults.style.display = 'none';
        billingQuantityInput.focus(); // Focus quantity input
    }

    function addToCart() {
        const quantity = parseInt(billingQuantityInput.value);

        if (!selectedBillingProduct) {
            alert('Please search and select a product.');
            return;
        }
        if (isNaN(quantity) || quantity <= 0) {
            alert('Please enter a valid quantity.');
            return;
        }
        if (quantity > selectedBillingProduct.stock) {
            alert(`Not enough stock. Only ${selectedBillingProduct.stock} available.`);
            return;
        }

        // Check if item already in cart
        const existingCartItemIndex = cart.findIndex(item => item.productId === selectedBillingProduct.id);

        if (existingCartItemIndex > -1) {
            // Update quantity if item exists
            const newQty = cart[existingCartItemIndex].qty + quantity;
             if (newQty > selectedBillingProduct.stock) {
                 alert(`Cannot add ${quantity} more. Total quantity (${newQty}) exceeds stock (${selectedBillingProduct.stock}).`);
                 return;
             }
            cart[existingCartItemIndex].qty = newQty;
            cart[existingCartItemIndex].total = cart[existingCartItemIndex].qty * cart[existingCartItemIndex].price;
        } else {
            // Add new item to cart
            cart.push({
                productId: selectedBillingProduct.id,
                name: selectedBillingProduct.name,
                qty: quantity,
                price: selectedBillingProduct.price,
                total: quantity * selectedBillingProduct.price
            });
        }

        renderCart();
        // Reset form
        billingProductSearch.value = '';
        selectedBillingProduct = null;
        billingQuantityInput.value = 1;
        billingProductSearch.focus();
    }

    function renderCart() {
        cartItemsTableBody.innerHTML = ''; // Clear cart table
        if (cart.length === 0) {
             cartItemsTableBody.innerHTML = '<tr><td colspan="5">Cart is empty</td></tr>';
        } else {
            cart.forEach((item, index) => {
                const row = cartItemsTableBody.insertRow();
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.qty}</td>
                    <td>${item.price.toFixed(2)}</td>
                    <td>${item.total.toFixed(2)}</td>
                    <td><button class="btn btn-danger btn-small" onclick="removeFromCart(${index})">X</button></td>
                `;
            });
        }
        updateCartTotals();
    }

     window.removeFromCart = (index) => {
         cart.splice(index, 1); // Remove item from cart array
         renderCart();
     }

     function updateCartTotals() {
         const subtotal = cart.reduce((sum, item) => sum + item.total, 0);
         const taxRate = settings.defaultTaxRate / 100;
         const taxAmount = subtotal * taxRate;
         const grandTotal = subtotal + taxAmount;

         cartSubtotalEl.textContent = `₹${subtotal.toFixed(2)}`;
         cartTaxAmountEl.textContent = `₹${taxAmount.toFixed(2)}`;
         cartGrandTotalEl.textContent = `₹${grandTotal.toFixed(2)}`;
         cartTaxRateEl.textContent = settings.defaultTaxRate; // Ensure rate display is current
     }

     function generateInvoice() {
         if (cart.length === 0) {
             alert('Cart is empty. Add items to generate an invoice.');
             return;
         }

         const subtotal = cart.reduce((sum, item) => sum + item.total, 0);
         const taxAmount = subtotal * (settings.defaultTaxRate / 100);
         const grandTotal = subtotal + taxAmount;
         const customerId = billingCustomerSelect.value;
         const customer = customers.find(c => c.id == customerId) || { id: 'walk-in', name: 'Walk-in Customer' };
         const invoiceNum = `INV-${String(nextInvoiceNum++).padStart(4, '0')}`;

         const newSale = {
             invoiceNum: invoiceNum,
             date: new Date().toISOString().split('T')[0], // YYYY-MM-DD
             customerId: customer.id,
             customerName: customer.name,
             items: [...cart], // Copy cart items
             total: subtotal,
             tax: taxAmount,
             grandTotal: grandTotal
         };

         // 1. Update Stock Levels
         cart.forEach(cartItem => {
             const productIndex = products.findIndex(p => p.id === cartItem.productId);
             if (productIndex > -1) {
                 products[productIndex].stock -= cartItem.qty;
             }
         });

         // 2. Add to Sales History
         salesHistory.unshift(newSale); // Add to beginning for recent first

         // 3. Show Preview (or print directly in real app)
         showInvoicePreview(newSale);

         // 4. Clear Cart and Billing Form
         cart = [];
         renderCart();
         billingProductSearch.value = '';
         selectedBillingProduct = null;
         billingQuantityInput.value = 1;
         billingCustomerSelect.value = 'walk-in'; // Reset customer
         document.getElementById('bill-invoice-num').textContent = `INV-${String(nextInvoiceNum).padStart(4, '0')}`; // Update next inv number display


         // 5. Update relevant displays
         renderProductTable(); // To show updated stock
         renderSalesHistoryTable(); // To show new sale
         updateDashboard(); // Update dashboard stats

     }

    function showInvoicePreview(sale) {
        document.getElementById('preview-invoice-num').textContent = sale.invoiceNum;
        document.getElementById('preview-shop-name').textContent = settings.shopName;
        document.getElementById('preview-date').textContent = sale.date;
        document.getElementById('preview-customer').textContent = sale.customerName;

        const itemsList = document.getElementById('preview-items-list');
        itemsList.innerHTML = '';
        sale.items.forEach(item => {
            const row = itemsList.insertRow();
            row.innerHTML = `
                <td>${item.name}</td>
                <td>${item.qty}</td>
                <td>${item.price.toFixed(2)}</td>
                <td>${item.total.toFixed(2)}</td>
            `;
        });

        document.getElementById('preview-subtotal').textContent = `₹${sale.total.toFixed(2)}`;
        document.getElementById('preview-tax').textContent = `₹${sale.tax.toFixed(2)} (@${settings.defaultTaxRate}%)`;
        document.getElementById('preview-total').textContent = `₹${sale.grandTotal.toFixed(2)}`;

        openModal('invoice-preview-modal');
    }


    // --- Sales History ---
    function renderSalesHistoryTable(filteredSales = salesHistory) {
         salesHistoryListBody.innerHTML = ''; // Clear existing rows
        if (filteredSales.length === 0) {
            salesHistoryListBody.innerHTML = '<tr><td colspan="5">No sales history found.</td></tr>';
            return;
        }
        filteredSales.forEach(s => {
            const row = salesHistoryListBody.insertRow();
            row.innerHTML = `
                <td>${s.invoiceNum}</td>
                <td>${s.date}</td>
                <td>${s.customerName}</td>
                <td>${s.grandTotal.toFixed(2)}</td>
                <td>
                    <button class="btn btn-small" onclick="viewInvoiceDetail('${s.invoiceNum}')">View</button>
                     <!-- Add reprint/PDF generation here -->
                </td>
            `;
        });
    }
     window.viewInvoiceDetail = (invoiceNum) => {
         const sale = salesHistory.find(s => s.invoiceNum === invoiceNum);
         if(sale) {
             showInvoicePreview(sale);
         } else {
             alert('Invoice not found.');
         }
     }

    // --- Dashboard ---
    function updateDashboard() {
        // Daily Sales (Simplified: sales from today)
        const today = new Date().toISOString().split('T')[0];
        const todaysSales = salesHistory.filter(s => s.date === today);
        const dailyTotal = todaysSales.reduce((sum, s) => sum + s.grandTotal, 0);
        dashboardDailySales.textContent = `₹${dailyTotal.toFixed(2)}`;

        // Monthly Revenue (Simplified: sales from current month)
        const currentMonth = new Date().toISOString().slice(0, 7); // YYYY-MM
        const monthlySales = salesHistory.filter(s => s.date.startsWith(currentMonth));
        const monthlyTotal = monthlySales.reduce((sum, s) => sum + s.grandTotal, 0);
        dashboardMonthlyRevenue.textContent = `₹${monthlyTotal.toFixed(2)}`;

        // Stock Alerts
        updateStockAlerts();

        // Recent Transactions
        const recentSales = salesHistory.slice(0, 5); // Get last 5
        dashboardRecentTransactions.innerHTML = '';
        if (recentSales.length === 0) {
            dashboardRecentTransactions.innerHTML = '<li>No recent transactions</li>';
        } else {
            recentSales.forEach(s => {
                const li = document.createElement('li');
                li.textContent = `${s.invoiceNum} - ${s.customerName} - ₹${s.grandTotal.toFixed(2)}`;
                dashboardRecentTransactions.appendChild(li);
            });
        }
    }

    function updateStockAlerts() {
        const lowStockProducts = products.filter(p => p.stock <= p.reorderLevel);
        dashboardStockAlerts.innerHTML = '';
        if (lowStockProducts.length === 0) {
            dashboardStockAlerts.innerHTML = '<li>No alerts</li>';
        } else {
            lowStockProducts.forEach(p => {
                const li = document.createElement('li');
                li.classList.add('stock-alert');
                li.textContent = `${p.name} (${p.code || 'No Code'}) - Stock: ${p.stock}`;
                dashboardStockAlerts.appendChild(li);
            });
        }
    }

    // --- Event Listeners Setup ---
    function setupEventListeners() {
        // Navigation
        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                showSection(link.dataset.target);
            });
        });

        // Modals
        addProductBtn.addEventListener('click', () => openModal('add-product-modal'));
        addCustomerBtn.addEventListener('click', () => openModal('add-customer-modal'));
        addCustomerQuickBtn.addEventListener('click', () => openModal('add-customer-modal'));
        closeModalBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                // Find the closest parent modal and close it
                closeModal(btn.closest('.modal').id);
            });
        });
        // Close modal if clicking outside the content area
        window.addEventListener('click', (event) => {
            if (event.target.classList.contains('modal')) {
                closeModal(event.target.id);
            }
        });


        // Forms
        addProductForm.addEventListener('submit', handleAddProduct);
        addCustomerForm.addEventListener('submit', handleAddCustomer);

         // Settings
        settingAddCategoryBtn.addEventListener('click', addCategory);
        saveSettingsBtn.addEventListener('click', saveSettings);

        // Billing
        billingProductSearch.addEventListener('input', handleBillingProductSearch);
        // Hide search results if clicking elsewhere
         document.addEventListener('click', (e) => {
            if (!billingProductSearch.contains(e.target) && !billingSearchResults.contains(e.target)) {
                billingSearchResults.style.display = 'none';
            }
         });
        addToCartBtn.addEventListener('click', addToCart);
        generateInvoiceBtn.addEventListener('click', generateInvoice);

         // Invoice Preview Actions (Simulated)
        printInvoiceBtn.addEventListener('click', () => alert('Printing invoice... (Simulation)'));
        sharePdfBtn.addEventListener('click', () => alert('Generating PDF... (Simulation)'));


         // Placeholder for Backup/Export
         document.getElementById('backup-data-btn').addEventListener('click', () => alert('Data backup simulation complete!'));
         document.getElementById('export-data-btn').addEventListener('click', () => alert('Data export simulation complete!'));

         // Placeholder for Sales History Filter
         document.getElementById('history-filter-btn').addEventListener('click', () => {
             alert('Date filtering requires more complex logic to handle date ranges correctly.');
             // Implement actual date filtering here if needed
             renderSalesHistoryTable(); // For now, just re-render all
         });
    }

    // --- Start the application ---
    init();
});