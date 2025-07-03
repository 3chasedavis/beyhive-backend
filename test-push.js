const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));

async function sendTestNotification(title, body) {
    try {
        const response = await fetch('http://localhost:3000/send-notification', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                title: title,
                body: body,
                data: {
                    type: 'test',
                    timestamp: new Date().toISOString()
                }
            })
        });
        
        const result = await response.json();
        console.log('✅ Push notification sent!');
        console.log('Result:', result);
    } catch (error) {
        console.error('❌ Error sending notification:', error);
    }
}

// Test different types of notifications
async function runTests() {
    console.log('🚀 Testing real push notifications...\n');
    
    // Test 1: Basic notification
    console.log('📱 Test 1: Basic notification');
    await sendTestNotification('Beyhive Alert', 'Welcome to the Beyhive! 🐝');
    
    console.log('\n⏳ Waiting 3 seconds...\n');
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // Test 2: Concert notification
    console.log('🎵 Test 2: Concert notification');
    await sendTestNotification('Concert Alert!', 'Beyoncé is about to take the stage! ✨');
    
    console.log('\n⏳ Waiting 3 seconds...\n');
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // Test 3: Song notification
    console.log('🎤 Test 3: Song notification');
    await sendTestNotification('Song Alert!', 'AMERICA HAS A PROBLEM is starting! 🇺🇸');
    
    console.log('\n✅ All tests completed!');
}

// Run tests if this file is executed directly
if (require.main === module) {
    runTests();
}

module.exports = { sendTestNotification }; 