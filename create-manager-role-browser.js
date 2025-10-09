// Script to create ROLE_MANAGER role
// Run this in the browser console after logging in

const createManagerRole = async () => {
    try {
        const token = localStorage.getItem('jwtToken');
        
        if (!token) {
            console.error('No JWT token found. Please login first.');
            return;
        }

        const roleData = {
            name: 'ROLE_MANAGER',
            isActive: true,
            isDeleted: false
        };

        console.log('Creating ROLE_MANAGER with data:', roleData);

        const response = await fetch('/api/roles', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(roleData)
        });

        if (response.ok) {
            const result = await response.json();
            console.log('✅ ROLE_MANAGER created successfully:', result);
            alert('ROLE_MANAGER created successfully!');
        } else {
            const errorText = await response.text();
            console.error('❌ Failed to create ROLE_MANAGER:', response.status, errorText);
            alert(`Failed to create ROLE_MANAGER: ${response.status} - ${errorText}`);
        }
    } catch (error) {
        console.error('❌ Error creating ROLE_MANAGER:', error);
        alert(`Error creating ROLE_MANAGER: ${error.message}`);
    }
};

// Run the function
createManagerRole();
