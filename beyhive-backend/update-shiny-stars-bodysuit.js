const fetch = require('node-fetch');

const OUTFIT_NAME = 'Shiny Stars Bodysuit';
const NEW_IMAGE_NAME = 'Screenshot 2025-07-05 at 11.28.39 AM';
const API_URL = 'https://beyhive-backend.onrender.com/api/outfits';

(async () => {
  // Fetch all outfits
  const res = await fetch(API_URL);
  const data = await res.json();
  if (!data.outfits) {
    console.error('Failed to fetch outfits:', data);
    return;
  }
  const outfit = data.outfits.find(o => o.name === OUTFIT_NAME);
  if (!outfit) {
    console.error(`Outfit named '${OUTFIT_NAME}' not found.`);
    return;
  }
  // Update the imageName
  const updated = { ...outfit, imageName: NEW_IMAGE_NAME };
  const updateRes = await fetch(`${API_URL}/${outfit.id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(updated),
  });
  const updateData = await updateRes.json();
  if (updateRes.ok) {
    console.log(`Updated '${OUTFIT_NAME}' imageName to '${NEW_IMAGE_NAME}'.`);
  } else {
    console.error('Failed to update outfit:', updateData);
  }
})(); 