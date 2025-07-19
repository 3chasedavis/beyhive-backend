let password = '';
function setPasswordAndLoadAll(pw) {
  password = pw;
  loadNotifications();
  loadUserCount();
  loadNotificationHistory();
  // Hide password field after first successful login
  document.getElementById('passwordDiv').style.display = 'none';
}
document.getElementById('pushForm').addEventListener('submit', function(e) {
  e.preventDefault();
  const submitBtn = this.querySelector('button[type="submit"]');
  submitBtn.disabled = true;
  if (!password) {
    // First time: get password from field
    const pw = document.getElementById('password').value;
    if (!pw) {
      document.getElementById('pushStatus').textContent = 'Please enter the admin password.';
      document.getElementById('pushStatus').className = 'error';
      submitBtn.disabled = false;
      return;
    }
    setPasswordAndLoadAll(pw);
  }
  const notifType = document.getElementById('notifType').value;
  const notifTitle = document.getElementById('notifTitle').value;
  const notifMessage = document.getElementById('notifMessage').value;
  fetch('/api/admin/notifications/send', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      password,
      notifType,
      title: notifTitle,
      message: notifMessage
    })
  })
  .then(res => res.json())
  .then(data => {
    submitBtn.disabled = false;
    if (data.success) {
      document.getElementById('pushStatus').textContent = 'Notification sent!';
      document.getElementById('pushStatus').className = 'success';
      loadNotificationHistory();
    } else {
      document.getElementById('pushStatus').textContent = 'Error: ' + (data.error || 'Unknown error');
      document.getElementById('pushStatus').className = 'error';
    }
  })
  .catch(err => {
    submitBtn.disabled = false;
    document.getElementById('pushStatus').textContent = 'Error: ' + err;
    document.getElementById('pushStatus').className = 'error';
  });
});
window.addEventListener('DOMContentLoaded', function() {
  // Existing password logic
  const urlParams = new URLSearchParams(window.location.search);
  const pw = urlParams.get('password');
  if (pw) {
    setPasswordAndLoadAll(pw);
  }

  // Preset button logic (moved here for reliability)
  document.querySelectorAll('.preset-btn').forEach(btn => {
    btn.onclick = function() {
      document.getElementById('notifTitle').value = btn.getAttribute('data-title');
      document.getElementById('notifMessage').value = btn.getAttribute('data-message');
      document.getElementById('notifType').value = btn.getAttribute('data-type');
    };
  });

  // --- Livestreams Management ---
  const platforms = ["TikTok", "Instagram", "YouTube", "Discord", "Twitch"];
  let livestreams = [];
  function renderRows(data) {
    const container = document.getElementById('livestreams');
    container.innerHTML = '';
    data.forEach((item, idx) => {
      const row = document.createElement('div');
      row.innerHTML = `
        <input type="text" value="${item.title || ''}" placeholder="Stream Title (e.g. @beyonceupdates)" onchange="updateTitle(${idx}, this.value)" style="width: 200px;" />
        <select onchange="updatePlatform(${idx}, this.value)">
          ${platforms.map(p => `<option${p === item.platform ? ' selected' : ''}>${p}</option>`).join('')}
        </select>
        <input type="text" value="${item.url || ''}" placeholder="Paste link here" onchange="updateUrl(${idx}, this.value)" />
        <button onclick="removeRow(${idx})">Remove</button>
      `;
      container.appendChild(row);
    });
  }
  function addRow() {
    livestreams.push({ title: '', platform: platforms[0], url: '' });
    renderRows(livestreams);
  }
  function removeRow(idx) {
    livestreams.splice(idx, 1);
    renderRows(livestreams);
  }
  function updateTitle(idx, value) {
    livestreams[idx].title = value;
  }
  function updatePlatform(idx, value) {
    livestreams[idx].platform = value;
  }
  function updateUrl(idx, value) {
    livestreams[idx].url = value;
  }
  function saveLivestreams() {
    fetch('/api/livestreams', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(livestreams)
    }).then(() => alert('Saved!'));
  }
  // Attach to window for global access
  window.addRow = addRow;
  window.removeRow = removeRow;
  window.updateTitle = updateTitle;
  window.updatePlatform = updatePlatform;
  window.updateUrl = updateUrl;
  window.saveLivestreams = saveLivestreams;
  fetch('/api/livestreams')
    .then(res => res.json())
    .then(data => { livestreams = data; renderRows(livestreams); });

  // === Calendar Events Management ===
  const eventForm = document.getElementById('eventForm');
  const eventTitle = document.getElementById('eventTitle');
  const eventDate = document.getElementById('eventDate');
  const eventTime = document.getElementById('eventTime');
  const eventLocation = document.getElementById('eventLocation');
  const eventDescription = document.getElementById('eventDescription');
  const eventTimezone = document.getElementById('eventTimezone');
  const eventStatus = document.getElementById('eventStatus');
  const eventFormStatus = document.getElementById('eventFormStatus');
  const eventsTable = document.getElementById('eventsTable');
  const eventsTableBody = eventsTable.querySelector('tbody');

  let editingEventId = null;

  function fetchEvents() {
    fetch('/api/events')
      .then(res => res.json())
      .then(data => {
        renderEvents(data.events || []);
      });
  }

  function renderEvents(events) {
    eventsTableBody.innerHTML = '';
    if (!events.length) {
      eventsTable.style.display = 'none';
      return;
    }
    eventsTable.style.display = '';
    events.forEach(event => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${event.title}</td>
        <td>${event.date}</td>
        <td>${event.time || ''}</td>
        <td>${event.location || ''}</td>
        <td>${event.description || ''}</td>
        <td>${event.timezone || ''}</td>
        <td>${event.status || 'upcoming'}</td>
        <td>
          <button onclick="editEvent('${event.id}')">Edit</button>
          <button class="remove-event-btn" onclick="deleteEvent('${event.id}')">Remove</button>
        </td>
      `;
      eventsTableBody.appendChild(tr);
    });
  }

  eventForm.onsubmit = function(e) {
    e.preventDefault();
    eventFormStatus.textContent = '';
    const data = {
      title: eventTitle.value,
      date: eventDate.value,
      time: eventTime.value,
      location: eventLocation.value,
      description: eventDescription.value,
      timezone: eventTimezone.value,
      status: eventStatus.value
    };
    if (editingEventId) {
      // Update event
      fetch(`/api/events/${editingEventId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(res => res.json())
        .then(result => {
          eventFormStatus.textContent = 'Event updated!';
          eventForm.reset();
          editingEventId = null;
          fetchEvents();
        });
    } else {
      // Add event
      // Generate a unique ID for the new event
      data.id = 'event_' + Date.now();
      fetch('/api/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(res => res.json())
        .then(result => {
          eventFormStatus.textContent = 'Event added!';
          eventForm.reset();
          fetchEvents();
        });
    }
  };

  window.editEvent = function(id) {
    fetch(`/api/events/${id}`)
      .then(res => res.json())
      .then(event => {
        eventTitle.value = event.title;
        eventDate.value = event.date;
        eventTime.value = event.time;
        eventLocation.value = event.location;
        eventDescription.value = event.description;
        eventTimezone.value = event.timezone || 'America/New_York';
        eventStatus.value = event.status || 'upcoming';
        editingEventId = id;
      });
  };

  window.deleteEvent = function(id) {
    if (!id) {
      console.error('deleteEvent called with invalid id:', id);
      alert('Error: Tried to delete an event with an invalid or missing ID.');
      return;
    }
    if (!confirm('Delete this event?')) return;
    fetch(`/api/events/${id}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(result => {
        fetchEvents();
      });
  };

  // Initial load
  fetchEvents();

  // === Outfits Management ===
  const outfitForm = document.getElementById('outfitForm');
  const outfitName = document.getElementById('outfitName');
  const outfitLocation = document.getElementById('outfitLocation');
  const outfitImageName = document.getElementById('outfitImageName');
  const outfitIsNew = document.getElementById('outfitIsNew');
  const outfitSection = document.getElementById('outfitSection');
  const outfitDescription = document.getElementById('outfitDescription');
  const outfitFormStatus = document.getElementById('outfitFormStatus');
  const outfitsTable = document.getElementById('outfitsTable');
  const outfitsTableBody = outfitsTable.querySelector('tbody');

  let editingOutfitId = null;

  function fetchOutfits() {
    fetch('/api/outfits')
      .then(res => res.json())
      .then(data => {
        console.log('Outfits data received:', data);
        renderOutfits(data.outfits || []);
      })
      .catch(err => {
        console.error('Error fetching outfits:', err);
        renderOutfits([]);
      });
  }

  function renderOutfits(outfits) {
    console.log('Rendering outfits:', outfits);
    outfitsTableBody.innerHTML = '';
    if (!outfits.length) {
      console.log('No outfits to display');
      outfitsTable.style.display = 'none';
      return;
    }
    outfitsTable.style.display = '';
    outfits.forEach(outfit => {
      console.log('Rendering outfit:', outfit);
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${outfit.name}</td>
        <td>${outfit.location}</td>
        <td>${outfit.imageUrl ? `<img src="${outfit.imageUrl}" style="max-width:100px;max-height:100px;" />` : 'No image'}</td>
        <td>${outfit.section}</td>
        <td>${outfit.isNew ? 'Yes' : 'No'}</td>
        <td>${outfit.description || ''}</td>
        <td>
          <button onclick="editOutfit('${outfit.id}')">Edit</button>
          <button class="remove-event-btn" onclick="deleteOutfit('${outfit.id}')">Remove</button>
        </td>
      `;
      outfitsTableBody.appendChild(tr);
    });
  }

  outfitForm.onsubmit = function(e) {
    e.preventDefault();
    outfitFormStatus.textContent = '';
    
    const formData = new FormData();
    formData.append('name', outfitName.value);
    formData.append('location', outfitLocation.value);
    formData.append('isNew', outfitIsNew.checked);
    formData.append('section', outfitSection.value);
    formData.append('description', outfitDescription.value);
    
    // Add image file if selected
    const imageFile = document.getElementById('outfitImage').files[0];
    if (imageFile) {
      formData.append('image', imageFile);
    }
    
    if (editingOutfitId) {
      // Update outfit
      fetch(`/api/outfits/${editingOutfitId}`, {
        method: 'PUT',
        body: formData
      })
        .then(res => res.json())
        .then(result => {
          outfitFormStatus.textContent = 'Outfit updated!';
          outfitForm.reset();
          editingOutfitId = null;
          fetchOutfits();
        });
    } else {
      // Add outfit
      // Generate a unique ID for the new outfit
      const newId = 'outfit_' + Date.now();
      formData.append('id', newId);
      fetch('/api/outfits', {
        method: 'POST',
        body: formData
      })
        .then(res => res.json())
        .then(result => {
          outfitFormStatus.textContent = 'Outfit added!';
          outfitForm.reset();
          fetchOutfits();
        });
    }
  };

  window.editOutfit = function(id) {
    if (!id) {
      console.error('editOutfit called with invalid id:', id);
      alert('Error: Tried to edit an outfit with an invalid or missing ID.');
      return;
    }
    fetch('/api/outfits')
      .then(res => res.json())
      .then(data => {
        const outfit = (data.outfits || []).find(o => o.id === id);
        if (!outfit) return;
        outfitName.value = outfit.name;
        outfitLocation.value = outfit.location;
        outfitIsNew.checked = !!outfit.isNew;
        outfitSection.value = outfit.section;
        outfitDescription.value = outfit.description || '';
        editingOutfitId = id;
        outfitFormStatus.textContent = 'Editing outfit...';
      });
  };

  window.deleteOutfit = function(id) {
    if (!id) {
      console.error('deleteOutfit called with invalid id:', id);
      alert('Error: Tried to delete an outfit with an invalid or missing ID.');
      return;
    }
    if (!confirm('Delete this outfit?')) return;
    fetch(`/api/outfits/${id}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(result => {
        fetchOutfits();
      });
  };

  // Initial load
  fetchOutfits();

  // === Partners Management ===
  const partnerForm = document.getElementById('partnerForm');
  const partnerName = document.getElementById('partnerName');
  const partnerDescription = document.getElementById('partnerDescription');
  const partnerIcon = document.getElementById('partnerIcon');
  const partnerLink = document.getElementById('partnerLink');
  const partnerFormStatus = document.getElementById('partnerFormStatus');
  const partnersTable = document.getElementById('partnersTable');
  const partnersTableBody = partnersTable.querySelector('tbody');

  let editingPartnerIndex = null;

  function fetchPartners() {
    fetch('/api/partners')
      .then(res => res.json())
      .then(data => {
        renderPartners(data.partners || []);
      })
      .catch(err => {
        console.error('Error fetching partners:', err);
        renderPartners([]);
      });
  }

  function renderPartners(partners) {
    partnersTableBody.innerHTML = '';
    if (!partners.length) {
      partnersTable.style.display = 'none';
      return;
    }
    partnersTable.style.display = '';
    partners.forEach((partner, index) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${partner.name}</td>
        <td>${partner.description}</td>
        <td>${partner.iconUrl ? `<img src="${partner.iconUrl}" style="max-width:50px;max-height:50px;" />` : 'No image'}</td>
        <td><a href="${partner.link}" target="_blank">${partner.link}</a></td>
        <td>
          <button onclick="editPartner(${index})">Edit</button>
          <button class="remove-event-btn" onclick="deletePartner(${index})">Remove</button>
        </td>
      `;
      partnersTableBody.appendChild(tr);
    });
  }

  partnerForm.onsubmit = function(e) {
    e.preventDefault();
    partnerFormStatus.textContent = '';
    
    const formData = new FormData();
    formData.append('name', partnerName.value);
    formData.append('description', partnerDescription.value);
    formData.append('link', partnerLink.value);
    
    // Add icon file if selected
    const iconFile = document.getElementById('partnerIcon').files[0];
    if (iconFile) {
      formData.append('icon', iconFile);
    }
    
    if (editingPartnerIndex !== null) {
      // Update partner (use correct endpoint and index)
      fetch(`/api/partners/${editingPartnerIndex}`, {
        method: 'PUT',
        body: formData
      })
        .then(res => res.json())
        .then(result => {
          partnerFormStatus.textContent = 'Partner updated!';
          partnerForm.reset();
          editingPartnerIndex = null;
          fetchPartners();
        });
    } else {
      // Add partner
      fetch('/api/partners', {
        method: 'POST',
        body: formData
      })
        .then(res => res.json())
        .then(result => {
          partnerFormStatus.textContent = 'Partner added!';
          partnerForm.reset();
          fetchPartners();
        });
    }
  };

  window.editPartner = function(index) {
    fetch('/api/partners')
      .then(res => res.json())
      .then(data => {
        const partner = data.partners[index];
        partnerName.value = partner.name;
        partnerDescription.value = partner.description;
        partnerLink.value = partner.link;
        // Note: Can't set file input value for security reasons
        editingPartnerIndex = index;
        partnerFormStatus.textContent = 'Editing partner... (Upload new image to change icon)';
      });
  };

  window.deletePartner = function(index) {
    if (confirm('Are you sure you want to delete this partner?')) {
      fetch(`/api/partners/${index}`, {
        method: 'DELETE'
      })
        .then(res => res.json())
        .then(result => {
          fetchPartners();
        });
    }
  };

  // Load partners on page load
  fetchPartners();

  // === Device Token Stats and Table ===
  const ADMIN_PASSWORD = 'chase3870';

  function fetchDeviceStats() {
    fetch('/api/admin/registereddevices')
      .then(res => res.json())
      .then(data => {
        document.getElementById('registeredDevices').textContent = 'App Downloads (Unique Devices): ' + (data.count || 0);
      });
    fetch('/api/admin/devicetokencount')
      .then(res => res.json())
      .then(data => {
        document.getElementById('deviceTokenCount').textContent = 'Total Device Tokens: ' + (data.count || 0);
      });
    fetch('/api/admin/onlinenow')
      .then(res => res.json())
      .then(data => {
        document.getElementById('activeUserCount').textContent = 'Active Users: ' + (data.count || 0);
      });
  }

  function fetchDeviceTokens() {
    fetch('/api/admin/device-tokens?password=' + encodeURIComponent(ADMIN_PASSWORD))
      .then(res => res.json())
      .then(tokens => {
        const table = document.getElementById('deviceTokensTable');
        if (!table) return;
        const tbody = table.querySelector('tbody');
        tbody.innerHTML = '';
        tokens.forEach(t => {
          const tr = document.createElement('tr');
          tr.innerHTML = `<td>${t.token}</td><td>${t.createdAt ? new Date(t.createdAt).toLocaleString() : ''}</td>`;
          tbody.appendChild(tr);
        });
        table.style.display = tokens.length ? '' : 'none';
      });
  }

  // === Update Overlay Control ===
  const updateOverlayForm = document.getElementById('updateOverlayForm');
  const updateRequiredToggle = document.getElementById('updateRequiredToggle');
  const minVersionInput = document.getElementById('minVersionInput');
  const updateOverlayStatus = document.getElementById('updateOverlayStatus');

  function fetchUpdateOverlay() {
    fetch('/api/admin/update-required')
      .then(res => res.json())
      .then(data => {
        updateRequiredToggle.checked = !!data.updateRequired;
        minVersionInput.value = data.minVersion || '';
      });
  }

  // Fix TypeError: check for null before setting onsubmit
  if (updateOverlayForm) {
    updateOverlayForm.onsubmit = function(e) {
      e.preventDefault();
      updateOverlayStatus.textContent = '';
      fetch('/api/admin/update-required', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          updateRequired: updateRequiredToggle.checked,
          minVersion: minVersionInput.value.trim()
        })
      })
        .then(res => res.json())
        .then(result => {
          updateOverlayStatus.textContent = 'Saved!';
          fetchUpdateOverlay();
        });
    };
  }

  fetchUpdateOverlay();

  // Call these on page load
  fetchDeviceStats();
  fetchDeviceTokens();
  // Comment out fetchGames() call
  // fetchGames();

  // === Album Rankings Management ===
  const albumRankingsSection = document.createElement('div');
  albumRankingsSection.innerHTML = '<h2>Manage Album Rankings</h2><table id="albumRankingsTable" style="width:100%; margin-bottom:30px; display:none;"><thead><tr><th>Nickname</th><th>Albums</th><th>Likes</th><th>Created</th><th>Actions</th></tr></thead><tbody></tbody></table>';
  document.body.insertBefore(albumRankingsSection, document.getElementById('deviceTokensSection'));
  const albumRankingsTable = document.getElementById('albumRankingsTable');
  const albumRankingsTbody = albumRankingsTable.querySelector('tbody');

  function fetchAlbumRankings() {
    fetch('/api/album-rankings')
      .then(res => res.json())
      .then(data => {
        renderAlbumRankings(data.rankings || []);
      });
  }

  function renderAlbumRankings(rankings) {
    albumRankingsTbody.innerHTML = '';
    if (!rankings.length) {
      albumRankingsTable.style.display = 'none';
      return;
    }
    albumRankingsTable.style.display = '';
    rankings.forEach(ranking => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${ranking.nickname}</td>
        <td><pre>${ranking.ranking.map(a => a.title).join('\n')}</pre></td>
        <td>${ranking.likes.length}</td>
        <td>${new Date(ranking.createdAt).toLocaleString()}</td>
        <td><button class="remove-event-btn" onclick="deleteAlbumRanking('${ranking.id}', this)">Delete</button></td>
      `;
      albumRankingsTbody.appendChild(tr);
    });
  }

  window.deleteAlbumRanking = function(id, btn) {
    if (!confirm('Are you sure you want to delete this album ranking?')) return;
    fetch(`/api/album-rankings/${id}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(result => {
        if (result.success) {
          btn.closest('tr').remove();
        } else {
          alert('Failed to delete: ' + (result.message || 'Unknown error'));
        }
      });
  };

  fetchAlbumRankings();

  // === Maintenance Mode Management ===
  const maintenanceToggle = document.getElementById('maintenanceToggle');
  const maintenanceStatus = document.getElementById('maintenanceStatus');

  function fetchMaintenanceMode() {
    fetch('/api/admin/maintenance-mode')
      .then(res => res.json())
      .then(data => {
        maintenanceToggle.checked = data.isMaintenanceMode;
      })
      .catch(err => {
        console.error('Error fetching maintenance mode:', err);
      });
  }

  window.saveMaintenanceMode = function() {
    const isMaintenanceMode = maintenanceToggle.checked;
    fetch('/api/admin/maintenance-mode', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ isMaintenanceMode })
    })
      .then(res => res.json())
      .then(data => {
        maintenanceStatus.textContent = 'Saved!';
        setTimeout(() => {
          maintenanceStatus.textContent = '';
        }, 2000);
      })
      .catch(err => {
        maintenanceStatus.textContent = 'Error saving';
        console.error('Error saving maintenance mode:', err);
      });
  };

  fetchMaintenanceMode();

  // === Countdown Mode Management ===
  const countdownToggle = document.getElementById('countdownToggle');
  const countdownStatus = document.getElementById('countdownStatus');

  function fetchCountdownMode() {
    fetch('/api/admin/countdown-mode')
      .then(res => res.json())
      .then(data => {
        countdownToggle.checked = data.isCountdownEnabled;
      })
      .catch(err => {
        console.error('Error fetching countdown mode:', err);
      });
  }

  window.saveCountdownMode = function() {
    const isCountdownEnabled = countdownToggle.checked;
    fetch('/api/admin/countdown-mode', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ isCountdownEnabled })
    })
      .then(res => res.json())
      .then(data => {
        countdownStatus.textContent = 'Saved!';
        setTimeout(() => {
          countdownStatus.textContent = '';
        }, 2000);
      })
      .catch(err => {
        countdownStatus.textContent = 'Error saving';
        console.error('Error saving countdown mode:', err);
      });
  };

  fetchCountdownMode();

  // === Quiz Manager ===
  const quizManager = document.getElementById('quizManager');
  if (quizManager) {
    function fetchAndRenderQuizzes() {
      fetch('/api/survivor-quiz')
        .then(res => res.json())
        .then(data => {
          if (!data.success) return;
          const container = document.getElementById('quizManager');
          container.innerHTML = '';
          data.quizzes.forEach(quiz => {
            const quizDiv = document.createElement('div');
            quizDiv.style.border = '1px solid #ccc';
            quizDiv.style.borderRadius = '10px';
            quizDiv.style.padding = '18px';
            quizDiv.style.marginBottom = '18px';
            quizDiv.style.background = '#fff';
            quizDiv.innerHTML = `
              <div style="font-weight:bold;font-size:1.2em;margin-bottom:8px;">${quiz.title}</div>
              <label>Open At: <input type="datetime-local" id="openAt_${quiz.id}" value="${quiz.openAt ? new Date(quiz.openAt).toISOString().slice(0,16) : ''}"></label>
              <label style="margin-left:16px;">Close At: <input type="datetime-local" id="closeAt_${quiz.id}" value="${quiz.closeAt ? new Date(quiz.closeAt).toISOString().slice(0,16) : ''}"></label>
              <button id="saveQuiz_${quiz.id}" style="margin-left:18px;">Save</button>
              <span id="quizStatus_${quiz.id}" style="margin-left:12px;color:green;"></span>
            `;
            container.appendChild(quizDiv);
            setTimeout(() => {
              document.getElementById(`saveQuiz_${quiz.id}`).onclick = function() {
                const openAt = document.getElementById(`openAt_${quiz.id}`).value;
                const closeAt = document.getElementById(`closeAt_${quiz.id}`).value;
                fetch(`/api/survivor-quiz/${quiz.id}`, {
                  method: 'PUT',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ openAt: openAt ? new Date(openAt).toISOString() : null, closeAt: closeAt ? new Date(closeAt).toISOString() : null })
                })
                .then(res => res.json())
                .then(resp => {
                  const status = document.getElementById(`quizStatus_${quiz.id}`);
                  if (resp.success) {
                    status.textContent = 'Saved!';
                    status.style.color = 'green';
                  } else {
                    status.textContent = 'Error saving.';
                    status.style.color = 'red';
                  }
                  setTimeout(() => { status.textContent = ''; }, 2000);
                });
              };
            }, 0);
          });
        });
    }
    // Call on page load
    fetchAndRenderQuizzes();
  }
});
function loadNotifications() {
  fetch('/api/admin/notifications/api?password=' + encodeURIComponent(password))
    .then(res => res.json())
    .then(data => {
      const tbody = document.querySelector('#notifTable tbody');
      tbody.innerHTML = '';
      data.forEach(n => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${new Date(n.receivedAt).toLocaleString()}</td>
                        <td>${n.data.notification_type || ''}</td>
                        <td><pre>${JSON.stringify(n.data, null, 2)}</pre></td>`;
        tbody.appendChild(tr);
      });
    });
}
function loadUserCount() {
  fetch('/api/admin/usercount?password=' + encodeURIComponent(password))
    .then(res => res.json())
    .then(data => {
      document.getElementById('userCount').textContent = `Total Users (App Downloads): ${data.count}`;
    });
  fetch('/api/admin/activeusercount?password=' + encodeURIComponent(password))
    .then(res => res.json())
    .then(data => {
      document.getElementById('activeUserCount').textContent = `Active Users: ${data.count}`;
    });
}
function loadNotificationHistory() {
  fetch('/api/admin/notificationhistory?password=' + encodeURIComponent(password))
    .then(res => res.json())
    .then(data => {
      const table = document.getElementById('historyTable');
      const tbody = table.querySelector('tbody');
      tbody.innerHTML = '';
      if (data.length > 0) {
        table.style.display = '';
        data.forEach(n => {
          const tr = document.createElement('tr');
          tr.innerHTML = `<td>${new Date(n.sentAt).toLocaleString()}</td><td>${n.notifType}</td><td>${n.title}</td><td>${n.message}</td>`;
          tbody.appendChild(tr);
        });
      } else {
        table.style.display = 'none';
      }
    });
} 