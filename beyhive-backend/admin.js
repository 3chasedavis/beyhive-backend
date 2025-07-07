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

  // === Calendar Events Management ===
  const eventForm = document.getElementById('eventForm');
  const eventTitle = document.getElementById('eventTitle');
  const eventDate = document.getElementById('eventDate');
  const eventTime = document.getElementById('eventTime');
  const eventLocation = document.getElementById('eventLocation');
  const eventDescription = document.getElementById('eventDescription');
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
      description: eventDescription.value
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
    fetch('/api/events')
      .then(res => res.json())
      .then(data => {
        const event = (data.events || []).find(e => e.id === id);
        if (!event) return;
        eventTitle.value = event.title;
        eventDate.value = event.date;
        eventTime.value = event.time || '';
        eventLocation.value = event.location || '';
        eventDescription.value = event.description || '';
        editingEventId = id;
        eventFormStatus.textContent = 'Editing event...';
      });
  };

  window.deleteEvent = function(id) {
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

  // === Survivor Games Management ===
  let games = [];
  let selectedGameId = null;
  let selectedQuestionId = null;
  let selectedEntryIndex = null;

  function fetchGames() {
    fetch('/api/survivor/games')
      .then(res => res.json())
      .then(data => {
        games = data;
        renderGames();
      });
  }

  function renderGames() {
    let list = document.getElementById('games-list');
    if (!list) {
      list = document.createElement('div');
      list.id = 'games-list';
      document.body.insertBefore(list, document.getElementById('questions-list'));
    }
    list.innerHTML = '';
    games.forEach(game => {
      const div = document.createElement('div');
      div.innerHTML = `<b>${game.name}</b> (${game.status}) \
        <button onclick="editGame('${game.id}')">Edit</button>\
        <button onclick="deleteGame('${game.id}')">Delete</button>\
        <button onclick="showQuestions('${game.id}')">Questions</button>`;
      list.appendChild(div);
    });
  }

  function showQuestions(gameId) {
    selectedGameId = gameId;
    const game = games.find(g => g.id === gameId);
    let list = document.getElementById('questions-list');
    if (!list) {
      list = document.createElement('div');
      list.id = 'questions-list';
      document.body.appendChild(list);
    }
    list.innerHTML = `<h4>Questions for ${game.name}</h4>`;
    game.questions.forEach(q => {
      const div = document.createElement('div');
      div.innerHTML = `${q.text} (${q.points} pts) [${q.choices.join(', ')}] \
        <button onclick=\"editQuestion('${q.id}')\">Edit</button>\
        <button onclick=\"deleteQuestion('${q.id}')\">Delete</button>`;
      list.appendChild(div);
    });
    list.innerHTML += `<button onclick=\"showAddQuestionForm()\">Add Question</button>`;

    // Show leaderboard only if game is completed
    if (game.status === 'completed') {
      document.getElementById('leaderboard-section').style.display = '';
      showLeaderboard(gameId);
    } else {
      document.getElementById('leaderboard-section').style.display = 'none';
    }
  }

  function showAddQuestionForm() {
    selectedQuestionId = null;
    let form = document.getElementById('question-form');
    if (!form) {
      form = document.createElement('div');
      form.id = 'question-form';
      document.body.appendChild(form);
    }
    form.innerHTML = `<h3>Add Question</h3>\
      <input id="question-text" placeholder="Question Text"><br>\
      <input id="question-points" type="number" placeholder="Points"><br>\
      <input id="question-choices" placeholder="Choices (comma separated)"><br>\
      <input id="question-correct" placeholder="Correct Answer"><br>\
      <button onclick="saveQuestion()">Save Question</button>\
      <button onclick="hideQuestionForm()">Cancel</button>`;
    form.style.display = '';
  }

  function editQuestion(qid) {
    const game = games.find(g => g.id === selectedGameId);
    const q = game.questions.find(q => q.id === qid);
    selectedQuestionId = qid;
    let form = document.getElementById('question-form');
    if (!form) {
      form = document.createElement('div');
      form.id = 'question-form';
      document.body.appendChild(form);
    }
    form.innerHTML = `<h3>Edit Question</h3>\
      <input id="question-text" value="${q.text}" placeholder="Question Text"><br>\
      <input id="question-points" type="number" value="${q.points}" placeholder="Points"><br>\
      <input id="question-choices" value="${q.choices.join(', ')}" placeholder="Choices (comma separated)"><br>\
      <input id="question-correct" value="${q.correctAnswer || ''}" placeholder="Correct Answer"><br>\
      <button onclick="saveQuestion()">Save Question</button>\
      <button onclick="hideQuestionForm()">Cancel</button>`;
    form.style.display = '';
  }

  function saveQuestion() {
    const text = document.getElementById('question-text').value;
    const points = parseInt(document.getElementById('question-points').value, 10) || 0;
    const choices = document.getElementById('question-choices').value.split(',').map(s => s.trim());
    const correctAnswer = document.getElementById('question-correct').value;
    const body = JSON.stringify({ text, points, choices, correctAnswer });
    const method = selectedQuestionId ? 'PUT' : 'POST';
    const url = selectedQuestionId
      ? `/api/survivor/games/${selectedGameId}/questions/${selectedQuestionId}`
      : `/api/survivor/games/${selectedGameId}/questions`;
    fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body })
      .then(() => { hideQuestionForm(); fetchGames(); showQuestions(selectedGameId); });
  }

  function deleteQuestion(qid) {
    if (!confirm('Delete this question?')) return;
    fetch(`/api/survivor/games/${selectedGameId}/questions/${qid}`, { method: 'DELETE' })
      .then(() => { fetchGames(); showQuestions(selectedGameId); });
  }

  function hideQuestionForm() {
    let form = document.getElementById('question-form');
    if (form) form.style.display = 'none';
  }

  function showLeaderboard(gameId) {
    const game = games.find(g => g.id === gameId);
    const table = document.getElementById('leaderboard-table');
    const sorted = [...(game.leaderboard || [])].sort((a, b) => b.points - a.points);
    table.innerHTML = '<tr><th>Rank</th><th>Name</th><th>Points</th><th>Actions</th></tr>';
    sorted.forEach((entry, idx) => {
      table.innerHTML += `<tr>\n      <td>${idx + 1}</td>\n      <td>${entry.name}</td>\n      <td>${entry.points}</td>\n      <td>\n        <button onclick=\"editEntry(${idx})\">Edit</button>\n        <button onclick=\"deleteEntry(${idx})\">Delete</button>\n      </td>\n    </tr>`;
    });
  }
  function showAddEntryForm() {
    selectedEntryIndex = null;
    document.getElementById('entry-name').value = '';
    document.getElementById('entry-points').value = '';
    document.getElementById('entry-form').style.display = '';
  }
  function editEntry(idx) {
    const game = games.find(g => g.id === selectedGameId);
    const entry = game.leaderboard[idx];
    selectedEntryIndex = idx;
    document.getElementById('entry-name').value = entry.name;
    document.getElementById('entry-points').value = entry.points;
    document.getElementById('entry-form').style.display = '';
  }
  function saveEntry() {
    const name = document.getElementById('entry-name').value;
    const points = parseInt(document.getElementById('entry-points').value, 10) || 0;
    const game = games.find(g => g.id === selectedGameId);
    if (!game.leaderboard) game.leaderboard = [];
    if (selectedEntryIndex === null) {
      game.leaderboard.push({ name, points });
    } else {
      game.leaderboard[selectedEntryIndex] = { name, points };
    }
    fetch(`/api/survivor/games/${selectedGameId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ leaderboard: game.leaderboard })
    }).then(() => {
      hideEntryForm();
      fetchGames();
      showLeaderboard(selectedGameId);
    });
  }
  function deleteEntry(idx) {
    if (!confirm('Delete this entry?')) return;
    const game = games.find(g => g.id === selectedGameId);
    game.leaderboard.splice(idx, 1);
    fetch(`/api/survivor/games/${selectedGameId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ leaderboard: game.leaderboard })
    }).then(() => {
      fetchGames();
      showLeaderboard(selectedGameId);
    });
  }
  function hideEntryForm() {
    document.getElementById('entry-form').style.display = 'none';
  }

  // Game management functions
  function showAddGameForm() {
    console.log('showAddGameForm called'); // Debug log
    alert('showAddGameForm called'); // Temporary alert to test if function is called
    let form = document.getElementById('game-form');
    if (!form) {
      form = document.createElement('div');
      form.id = 'game-form';
      form.style.cssText = 'background: #fff3cd; padding: 20px; border-radius: 8px; margin: 20px 0; border: 2px solid #e6b800; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 1000;';
      document.body.appendChild(form);
    }
    form.innerHTML = `<h3>Add New Game</h3>\
      <input id="game-name" placeholder="Game Name" style="width: 100%; margin-bottom: 10px; padding: 8px;"><br>\
      <select id="game-status" style="width: 100%; margin-bottom: 10px; padding: 8px;">\
        <option value="active">Active</option>\
        <option value="completed">Completed</option>\
      </select><br>\
      <button onclick="saveGame()" style="background: #e6b800; color: #fff; border: none; padding: 10px 20px; border-radius: 4px; margin-right: 10px;">Save Game</button>\
      <button onclick="hideGameForm()" style="background: #ccc; color: #fff; border: none; padding: 10px 20px; border-radius: 4px;">Cancel</button>`;
    form.style.display = 'block';
    console.log('Form should be visible now'); // Debug log
  }

  function editGame(gameId) {
    const game = games.find(g => g.id === gameId);
    let form = document.getElementById('game-form');
    if (!form) {
      form = document.createElement('div');
      form.id = 'game-form';
      document.body.appendChild(form);
    }
    form.innerHTML = `<h3>Edit Game</h3>\
      <input id="game-name" value="${game.name}" placeholder="Game Name"><br>\
      <select id="game-status">\
        <option value="active"${game.status === 'active' ? ' selected' : ''}>Active</option>\
        <option value="completed"${game.status === 'completed' ? ' selected' : ''}>Completed</option>\
      </select><br>\
      <button onclick="saveGame('${gameId}')">Save Game</button>\
      <button onclick="hideGameForm()">Cancel</button>`;
    form.style.display = '';
  }

  function saveGame(gameId = null) {
    const name = document.getElementById('game-name').value;
    const status = document.getElementById('game-status').value;
    const method = gameId ? 'PUT' : 'POST';
    const url = gameId ? `/api/survivor/games/${gameId}` : '/api/survivor/games';
    const body = JSON.stringify({ name, status });
    
    fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body })
      .then(() => { hideGameForm(); fetchGames(); });
  }

  function deleteGame(gameId) {
    if (!confirm('Delete this game?')) return;
    fetch(`/api/survivor/games/${gameId}`, { method: 'DELETE' })
      .then(() => { fetchGames(); });
  }

  function hideGameForm() {
    let form = document.getElementById('game-form');
    if (form) form.style.display = 'none';
  }

  window.fetchGames = fetchGames;
  window.showQuestions = showQuestions;
  window.showAddQuestionForm = showAddQuestionForm;
  window.editQuestion = editQuestion;
  window.deleteQuestion = deleteQuestion;
  window.showAddEntryForm = showAddEntryForm;
  window.editEntry = editEntry;
  window.deleteEntry = deleteEntry;
  window.saveEntry = saveEntry;
  window.saveQuestion = saveQuestion;
  window.hideEntryForm = hideEntryForm;
  window.hideQuestionForm = hideQuestionForm;
  window.showAddGameForm = showAddGameForm;
  window.editGame = editGame;
  window.deleteGame = deleteGame;
  window.saveGame = saveGame;
  window.hideGameForm = hideGameForm;

  // Call these on page load
  fetchDeviceStats();
  fetchDeviceTokens();
  fetchGames(); // Fetch games on page load
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