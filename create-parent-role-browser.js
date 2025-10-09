// Create ROLE_PARENT using existing JWT token from browser session
async function createParentRole() {
    try {
        // Get existing JWT token from localStorage or sessionStorage
        const token = localStorage.getItem('jwtToken') || 
                     localStorage.getItem('token') || 
                     sessionStorage.getItem('jwtToken') || 
                     sessionStorage.getItem('token');
        
        if (!token) {
            console.error('❌ No JWT token found. Please login first.');
            alert('❌ No JWT token found. Please login first.');
            return;
        }
        
        console.log('🔐 Using existing JWT token:', token.substring(0, 20) + '...');
        
        // Create ROLE_PARENT
        const response = await fetch('/api/roles', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: 'ROLE_PARENT',
                description: 'Parent role for school management system'
            })
        });
        
        console.log('📡 Response status:', response.status);
        
        if (response.ok) {
            const result = await response.json();
            console.log('✅ ROLE_PARENT created successfully!', result);
            alert('✅ ROLE_PARENT created successfully!');
            
            // Refresh the roles list if the function exists
            if (typeof loadRoles === 'function') {
                loadRoles();
            }
        } else {
            const error = await response.json();
            console.error('❌ Failed to create ROLE_PARENT:', error);
            alert('❌ Failed to create ROLE_PARENT: ' + (error.message || 'Unknown error'));
        }
        
    } catch (error) {
        console.error('❌ Error creating ROLE_PARENT:', error);
        alert('❌ Error creating ROLE_PARENT: ' + error.message);
    }
}

// Call the function immediately
createParentRole();
