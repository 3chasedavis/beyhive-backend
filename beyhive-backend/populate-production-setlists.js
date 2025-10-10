const https = require('https');

const BASE_URL = 'https://beyhive-backend.onrender.com/api';

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

function makeRequest(method, path, data = null) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'beyhive-backend.onrender.com',
      port: 443,
      path: path,
      method: method,
      headers: {
        'Content-Type': 'application/json',
      }
    };

    if (data) {
      const postData = JSON.stringify(data);
      options.headers['Content-Length'] = Buffer.byteLength(postData);
    }

    const req = https.request(options, (res) => {
      let responseData = '';
      res.on('data', (chunk) => {
        responseData += chunk;
      });
      res.on('end', () => {
        try {
          const parsed = JSON.parse(responseData);
          resolve({ status: res.statusCode, data: parsed });
        } catch (e) {
          resolve({ status: res.statusCode, data: responseData });
        }
      });
    });

    req.on('error', (err) => {
      reject(err);
    });

    if (data) {
      req.write(JSON.stringify(data));
    }
    req.end();
  });
}

async function populateSetlists() {
  try {
    console.log('Starting production setlist population...');
    
    // First, get existing setlists to clear them
    console.log('Fetching existing setlists...');
    const existingResponse = await makeRequest('GET', '/api/setlists');
    console.log(`Found ${existingResponse.data.setlists?.length || 0} existing setlists`);
    
    // Delete existing setlists
    if (existingResponse.data.setlists && existingResponse.data.setlists.length > 0) {
      console.log('Deleting existing setlists...');
      for (const setlist of existingResponse.data.setlists) {
        try {
          await makeRequest('DELETE', `/api/setlists/${setlist.id}`);
          console.log(`Deleted setlist: ${setlist.title}`);
        } catch (e) {
          console.log(`Failed to delete setlist ${setlist.title}: ${e.message}`);
        }
      }
    }
    
    // Add new setlists
    console.log('Adding new setlists...');
    for (const setlistData of currentSetlists) {
      try {
        const response = await makeRequest('POST', '/api/setlists', setlistData);
        if (response.status === 200 || response.status === 201) {
          console.log(`✅ Added setlist: ${setlistData.title} (${setlistData.songs.length} songs)`);
        } else {
          console.log(`❌ Failed to add setlist ${setlistData.title}: ${response.status} - ${JSON.stringify(response.data)}`);
        }
      } catch (e) {
        console.log(`❌ Error adding setlist ${setlistData.title}: ${e.message}`);
      }
    }
    
    // Verify the data
    console.log('Verifying setlists...');
    const verifyResponse = await makeRequest('GET', '/api/setlists');
    const totalSetlists = verifyResponse.data.setlists?.length || 0;
    const totalSongs = verifyResponse.data.setlists?.reduce((total, setlist) => total + setlist.songs.length, 0) || 0;
    
    console.log('✅ Production setlist population completed!');
    console.log(`Total setlists created: ${totalSetlists}`);
    console.log(`Total songs: ${totalSongs}`);
    
    if (totalSetlists === currentSetlists.length) {
      console.log('🎉 All setlists successfully populated!');
    } else {
      console.log('⚠️  Some setlists may not have been created successfully');
    }
    
  } catch (error) {
    console.error('❌ Error populating production setlists:', error);
  }
}

// Run the population
populateSetlists();

