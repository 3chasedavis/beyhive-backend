const mongoose = require('mongoose');
const Setlist = require('./models/Setlist');

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/beyhive-alert', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});

// Current setlist data from the app
const currentSetlists = [
  {
    id: 'act1-intro',
    title: 'Act 1 - Intro',
    songs: [
      { name: "Intro (contains elements of 'AMERIICAN REQUIEM')", order: 1, notes: null },
      { name: 'AMERIICAN REQUIEM', order: 2, notes: null },
      { name: 'Blackbird (The Beatles cover) ("COWBOY CARTER" version)', order: 3, notes: null },
      { name: "The Star-Spangled Banner (John Stafford Smith & Francis Scott Key cover) (includes elements of Jimi Hendrix's instrumental arrangement originally performed at Woodstock)", order: 4, notes: null },
      { name: 'Freedom (shortened)', order: 5, notes: null },
      { name: 'YA YA / Why Don\'t You Love Me', order: 6, notes: null },
      { name: 'Song played from tape: OH LOUISIANA', order: 7, notes: null }
    ],
    isActive: true
  },
  {
    id: 'act2-revolution',
    title: 'Act 2 - Revolution',
    songs: [
      { name: "PROPAGANDA (contains elements of Those Guys' 'An American Poem' and Death Grips' 'You Might Think He Loves…')", order: 1, notes: null },
      { name: "AMERICA HAS A PROBLEM (contains elements of 'AMERICA HAS A PROBLEM (feat. Kendrick Lamar)' & 'SPAGHETTII')", order: 2, notes: null },
      { name: "SPAGHETTII (contains elements of 'ESSA TÁ QUENTE', 'WTHELLY', 'Flawless', 'Run the World (Girls)' & 'MY POWER')", order: 3, notes: null },
      { name: 'Formation (shortened)', order: 4, notes: null },
      { name: "MY HOUSE (contains elements of Wisp's 'Your Face' and 'Bow Down')", order: 5, notes: null },
      { name: 'Diva', order: 6, notes: null }
    ],
    isActive: true
  },
  {
    id: 'act3-refuge-trailer',
    title: 'Act 3 - Refuge TRAILER',
    songs: [
      { name: "TRAILER (contains elements of Justice's 'Genesis', JPEGMAFIA's 'don't rely on other men' and 'I Been On')", order: 1, notes: null },
      { name: 'ALLIIGATOR TEARS (shortened)', order: 2, notes: null },
      { name: 'JUST FOR FUN (shortened)', order: 3, notes: null },
      { name: "PROTECTOR (with Rumi Carter) (contains elements of 'Dangerously In Love 2')", order: 4, notes: null },
      { name: 'Song played from tape: The First Time Ever I Saw Your Face (Ewan MacColl & Peggy Seeger song) (Roberta Flack version)', order: 5, notes: null },
      { name: 'FLAMENCO', order: 6, notes: null }
    ],
    isActive: true
  },
  {
    id: 'act4-marfa',
    title: 'Act 4 - Marfa',
    songs: [
      { name: 'II MOST WANTED (with Miley Cyrus)', order: 1, notes: null },
      { name: 'LEVII\'S JEANS (with Post Malone)', order: 2, notes: null },
      { name: 'FLAMENCO (shortened)', order: 3, notes: null },
      { name: 'Song played from tape: Déjà Vu (with Blue Ivy Carter) (dance Interlude)', order: 4, notes: null },
      { name: 'YA YA', order: 5, notes: null },
      { name: 'OH LOUISIANA', order: 6, notes: null },
      { name: 'DESERT EAGLE', order: 7, notes: null },
      { name: 'RIIVERDANCE', order: 8, notes: null },
      { name: 'II HANDS II HEAVEN', order: 9, notes: null },
      { name: 'TYRANT', order: 10, notes: null },
      { name: 'SWEET ★ HONEY ★ BUCKIIN\'', order: 11, notes: null },
      { name: 'AMEN', order: 12, notes: null }
    ],
    isActive: true
  }
];

async function populateSetlists() {
  try {
    console.log('Starting setlist population...');
    
    // Clear existing setlists
    await Setlist.deleteMany({});
    console.log('Cleared existing setlists');
    
    // Insert new setlists
    for (const setlistData of currentSetlists) {
      const setlist = new Setlist(setlistData);
      await setlist.save();
      console.log(`Added setlist: ${setlist.title} (${setlist.songs.length} songs)`);
    }
    
    console.log('✅ Setlist population completed successfully!');
    console.log(`Total setlists created: ${currentSetlists.length}`);
    
    // Verify the data
    const totalSetlists = await Setlist.countDocuments();
    const totalSongs = await Setlist.aggregate([
      { $unwind: '$songs' },
      { $count: 'totalSongs' }
    ]);
    
    console.log(`Verification: ${totalSetlists} setlists, ${totalSongs[0]?.totalSongs || 0} total songs`);
    
  } catch (error) {
    console.error('❌ Error populating setlists:', error);
  } finally {
    mongoose.connection.close();
    console.log('Database connection closed');
  }
}

// Run the population
populateSetlists();

