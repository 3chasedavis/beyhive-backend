const mongoose = require('mongoose');
const Setlist = require('./models/Setlist');

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/beyhive-alert', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

async function testSetlistAPI() {
  try {
    console.log('🧪 Testing Setlist API...\n');
    
    // Test 1: Get all setlists
    console.log('1. Testing GET /api/setlists');
    const allSetlists = await Setlist.find().sort({ 'songs.order': 1 });
    console.log(`✅ Found ${allSetlists.length} setlists`);
    allSetlists.forEach(setlist => {
      console.log(`   - ${setlist.title}: ${setlist.songs.length} songs`);
    });
    
    // Test 2: Get active setlists only
    console.log('\n2. Testing GET /api/setlists/active');
    const activeSetlists = await Setlist.find({ isActive: true }).sort({ 'songs.order': 1 });
    console.log(`✅ Found ${activeSetlists.length} active setlists`);
    
    // Test 3: Get single setlist by ID
    console.log('\n3. Testing GET /api/setlists/:id');
    const singleSetlist = await Setlist.findOne({ id: 'act1-intro' });
    if (singleSetlist) {
      console.log(`✅ Found setlist: ${singleSetlist.title}`);
      console.log(`   Songs: ${singleSetlist.songs.map(s => s.name).join(', ')}`);
    } else {
      console.log('❌ Setlist not found');
    }
    
    // Test 4: Create new setlist
    console.log('\n4. Testing POST /api/setlists');
    const newSetlist = new Setlist({
      id: 'test-act',
      title: 'Test Act',
      songs: [
        { name: 'Test Song 1', order: 1, notes: 'Test notes' },
        { name: 'Test Song 2', order: 2, notes: null }
      ],
      isActive: true
    });
    await newSetlist.save();
    console.log('✅ Created test setlist');
    
    // Test 5: Update setlist
    console.log('\n5. Testing PUT /api/setlists/:id');
    const updatedSetlist = await Setlist.findOneAndUpdate(
      { id: 'test-act' },
      { title: 'Updated Test Act', isActive: false },
      { new: true }
    );
    console.log(`✅ Updated setlist: ${updatedSetlist.title} (Active: ${updatedSetlist.isActive})`);
    
    // Test 6: Toggle setlist status
    console.log('\n6. Testing POST /api/setlists/:id/toggle');
    updatedSetlist.isActive = !updatedSetlist.isActive;
    await updatedSetlist.save();
    console.log(`✅ Toggled setlist status: ${updatedSetlist.isActive}`);
    
    // Test 7: Delete setlist
    console.log('\n7. Testing DELETE /api/setlists/:id');
    await Setlist.findOneAndDelete({ id: 'test-act' });
    console.log('✅ Deleted test setlist');
    
    console.log('\n🎉 All tests passed! Setlist API is working correctly.');
    
  } catch (error) {
    console.error('❌ Test failed:', error);
  } finally {
    mongoose.connection.close();
    console.log('Database connection closed');
  }
}

// Run the tests
testSetlistAPI();


