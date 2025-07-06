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
// On page load, if password is in URL, use it
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
    livestreams.push({ platform: platforms[0], url: '' });
    renderRows(livestreams);
  }
  function removeRow(idx) {
    livestreams.splice(idx, 1);
    renderRows(livestreams);
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
  window.updatePlatform = updatePlatform;
  window.updateUrl = updateUrl;
  window.saveLivestreams = saveLivestreams;
  fetch('/api/livestreams')
    .then(res => res.json())
    .then(data => { livestreams = data; renderRows(livestreams); });

  // === Calendar Events Management (Batch Editable) ===
  let events = [];
  function renderEventRows(data) {
    const container = document.getElementById('events');
    container.innerHTML = '';
    data.forEach((item, idx) => {
      const row = document.createElement('div');
      row.innerHTML = `
        <input type="text" value="${item.title || ''}" placeholder="Title" onchange="updateEventField(${idx}, 'title', this.value)" />
        <input type="date" value="${item.date || ''}" placeholder="Date" onchange="updateEventField(${idx}, 'date', this.value)" />
        <input type="text" value="${item.description || ''}" placeholder="Description" onchange="updateEventField(${idx}, 'description', this.value)" />
        <button onclick="removeEventRow(${idx})">Remove</button>
      `;
      container.appendChild(row);
    });
  }
  function addEventRow() {
    events.push({ title: '', date: '', description: '' });
    renderEventRows(events);
  }
  function removeEventRow(idx) {
    events.splice(idx, 1);
    renderEventRows(events);
  }
  function updateEventField(idx, field, value) {
    events[idx][field] = value;
  }
  function saveEvents() {
    // Add IDs if missing
    events.forEach(ev => { if (!ev.id) ev.id = Date.now().toString() + Math.floor(Math.random()*10000); });
    fetch('/api/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(events)
    }).then(() => alert('Saved!'));
  }
  // Attach to window for global access
  window.addEventRow = addEventRow;
  window.removeEventRow = removeEventRow;
  window.updateEventField = updateEventField;
  window.saveEvents = saveEvents;
  fetch('/api/events')
    .then(res => res.json())
    .then(data => { events = data.events || []; renderEventRows(events); });

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
        renderOutfits(data.outfits || []);
      });
  }

  function renderOutfits(outfits) {
    outfitsTableBody.innerHTML = '';
    if (!outfits.length) {
      outfitsTable.style.display = 'none';
      return;
    }
    outfitsTable.style.display = '';
    outfits.forEach(outfit => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${outfit.name}</td>
        <td>${outfit.location}</td>
        <td>${outfit.imageName}</td>
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
    const data = {
      name: outfitName.value,
      location: outfitLocation.value,
      imageName: outfitImageName.value,
      isNew: outfitIsNew.checked,
      section: outfitSection.value,
      description: outfitDescription.value
    };
    if (editingOutfitId) {
      // Update outfit
      fetch(`/api/outfits/${editingOutfitId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
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
      fetch('/api/outfits', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
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
    fetch('/api/outfits')
      .then(res => res.json())
      .then(data => {
        const outfit = (data.outfits || []).find(o => o.id === id);
        if (!outfit) return;
        outfitName.value = outfit.name;
        outfitLocation.value = outfit.location;
        outfitImageName.value = outfit.imageName;
        outfitIsNew.checked = !!outfit.isNew;
        outfitSection.value = outfit.section;
        outfitDescription.value = outfit.description || '';
        editingOutfitId = id;
        outfitFormStatus.textContent = 'Editing outfit...';
      });
  };

  window.deleteOutfit = function(id) {
    if (!confirm('Delete this outfit?')) return;
    fetch(`/api/outfits/${id}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(result => {
        fetchOutfits();
      });
  };

  // Initial load
  fetchOutfits();

  // === Device Token Stats and Table ===
  const ADMIN_PASSWORD = 'chase3870';

  function fetchDeviceStats() {
    fetch('/api/admin/registereddevices')
      .then(res => res.json())
      .then(data => {
        document.getElementById('registeredDevices').textContent = 'Registered Devices: ' + (data.count || 0);
      });
    fetch('/api/admin/devicetokencount')
      .then(res => res.json())
      .then(data => {
        document.getElementById('deviceTokenCount').textContent = 'Total Device Tokens: ' + (data.count || 0);
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

  fetchUpdateOverlay();

  // Call these on page load
  fetchDeviceStats();
  fetchDeviceTokens();
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