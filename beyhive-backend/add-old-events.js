const fetch = require('node-fetch');

const events = [
  { title: 'Los Angeles', date: '2025-04-28', time: '19:00', location: 'SoFi Stadium, Los Angeles, CA', description: 'Past event', timezone: 'America/Los_Angeles' },
  { title: 'Los Angeles', date: '2025-05-01', time: '19:00', location: 'SoFi Stadium, Los Angeles, CA', description: 'Past event', timezone: 'America/Los_Angeles' },
  { title: 'Los Angeles', date: '2025-05-04', time: '19:00', location: 'SoFi Stadium, Los Angeles, CA', description: 'Past event', timezone: 'America/Los_Angeles' },
  { title: 'Los Angeles', date: '2025-05-07', time: '19:00', location: 'SoFi Stadium, Los Angeles, CA', description: 'Past event', timezone: 'America/Los_Angeles' },
  { title: 'Los Angeles', date: '2025-05-09', time: '19:00', location: 'SoFi Stadium, Los Angeles, CA', description: 'Past event', timezone: 'America/Los_Angeles' },
  { title: 'Chicago', date: '2025-05-15', time: '19:30', location: 'Soldier Field, Chicago, IL', description: 'Past event', timezone: 'America/Chicago' },
  { title: 'Chicago', date: '2025-05-17', time: '19:30', location: 'Soldier Field, Chicago, IL', description: 'Past event', timezone: 'America/Chicago' },
  { title: 'Chicago', date: '2025-05-18', time: '19:30', location: 'Soldier Field, Chicago, IL', description: 'Past event', timezone: 'America/Chicago' },
  { title: 'New Jersey', date: '2025-05-22', time: '19:00', location: 'MetLife Stadium, East Rutherford, NJ', description: 'Past event', timezone: 'America/New_York' },
  { title: 'New Jersey', date: '2025-05-24', time: '19:00', location: 'MetLife Stadium, East Rutherford, NJ', description: 'Past event', timezone: 'America/New_York' },
  { title: 'New Jersey', date: '2025-05-25', time: '19:00', location: 'MetLife Stadium, East Rutherford, NJ', description: 'Past event', timezone: 'America/New_York' },
  { title: 'New Jersey', date: '2025-05-28', time: '19:00', location: 'MetLife Stadium, East Rutherford, NJ', description: 'Past event', timezone: 'America/New_York' },
  { title: 'New Jersey', date: '2025-05-29', time: '19:00', location: 'MetLife Stadium, East Rutherford, NJ', description: 'Past event', timezone: 'America/New_York' },
  { title: 'London', date: '2025-06-05', time: '19:00', location: 'Tottenham Hotspur Stadium, London, UK', description: 'Past event', timezone: 'Europe/London' },
  { title: 'London', date: '2025-06-07', time: '19:00', location: 'Tottenham Hotspur Stadium, London, UK', description: 'Past event', timezone: 'Europe/London' },
  { title: 'London', date: '2025-06-10', time: '19:00', location: 'Tottenham Hotspur Stadium, London, UK', description: 'Past event', timezone: 'Europe/London' },
  { title: 'London', date: '2025-06-12', time: '19:00', location: 'Tottenham Hotspur Stadium, London, UK', description: 'Past event', timezone: 'Europe/London' },
  { title: 'London', date: '2025-06-14', time: '19:00', location: 'Tottenham Hotspur Stadium, London, UK', description: 'Past event', timezone: 'Europe/London' },
  { title: 'London', date: '2025-06-16', time: '19:00', location: 'Tottenham Hotspur Stadium, London, UK', description: 'Past event', timezone: 'Europe/London' },
  { title: 'Paris', date: '2025-06-19', time: '20:00', location: 'Stade de France, Paris, France', description: 'Past event', timezone: 'Europe/Paris' },
  { title: 'Paris', date: '2025-06-21', time: '20:00', location: 'Stade de France, Paris, France', description: 'Past event', timezone: 'Europe/Paris' },
  { title: 'Paris', date: '2025-06-22', time: '20:00', location: 'Stade de France, Paris, France', description: 'Past event', timezone: 'Europe/Paris' },
  { title: 'Houston', date: '2025-06-28', time: '19:30', location: 'NRG Stadium, Houston, TX', description: 'Past event', timezone: 'America/Chicago' },
  { title: 'Houston', date: '2025-06-29', time: '19:30', location: 'NRG Stadium, Houston, TX', description: 'Past event', timezone: 'America/Chicago' },
  { title: 'Washington DC', date: '2025-07-04', time: '19:00', location: 'FedExField, Landover, MD', description: 'Past event', timezone: 'America/New_York' },
  { title: 'Washington DC', date: '2025-07-07', time: '19:00', location: 'FedExField, Landover, MD', description: 'Upcoming event', timezone: 'America/New_York' },
  { title: 'Atlanta', date: '2025-07-10', time: '19:30', location: 'Mercedes-Benz Stadium, Atlanta, GA', description: 'Upcoming event', timezone: 'America/New_York' },
  { title: 'Atlanta', date: '2025-07-11', time: '19:30', location: 'Mercedes-Benz Stadium, Atlanta, GA', description: 'Upcoming event', timezone: 'America/New_York' },
  { title: 'Atlanta', date: '2025-07-13', time: '19:30', location: 'Mercedes-Benz Stadium, Atlanta, GA', description: 'Upcoming event', timezone: 'America/New_York' },
  { title: 'Atlanta', date: '2025-07-14', time: '19:30', location: 'Mercedes-Benz Stadium, Atlanta, GA', description: 'Upcoming event', timezone: 'America/New_York' },
  { title: 'Las Vegas', date: '2025-07-25', time: '20:00', location: 'Allegiant Stadium, Paradise, NV', description: 'Upcoming event', timezone: 'America/Los_Angeles' },
  { title: 'Las Vegas', date: '2025-07-26', time: '20:00', location: 'Allegiant Stadium, Paradise, NV', description: 'Upcoming event', timezone: 'America/Los_Angeles' },
];

const API_URL = 'https://beyhive-backend.onrender.com/api/events';

(async () => {
  for (const event of events) {
    const res = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(event),
    });
    const data = await res.json();
    if (res.ok) {
      console.log(`Added: ${event.title} on ${event.date}`);
    } else {
      console.error(`Failed to add: ${event.title} on ${event.date}`, data);
    }
  }
})(); 