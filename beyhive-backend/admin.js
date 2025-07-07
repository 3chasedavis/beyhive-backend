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
        console.log('[DEBUG] fetchEvents data:', data);
        renderEvents(data.events || []);
      });
  }

  function renderEvents(events) {
    console.log('[DEBUG] renderEvents called with:', events);
    eventsTableBody.innerHTML = '';
    eventsTable.style.display = '';
    if (!events.length) {
      // No events, just show empty tbody
      return;
    }
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
        console.log('[DEBUG] fetchOutfits data:', data);
        renderOutfits(data.outfits || []);
      });
  }

  function renderOutfits(outfits) {
    console.log('[DEBUG] renderOutfits called with:', outfits);
    outfitsTableBody.innerHTML = '';
    outfitsTable.style.display = '';
    if (!outfits.length) {
      // No outfits, just show empty tbody
      return;
    }
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
  const gameForm = document.getElementById('gameForm');
  const gameName = document.getElementById('gameName');
  const gameStatus = document.getElementById('gameStatus');
  const gameIsNew = document.getElementById('gameIsNew');
  const gameFormStatus = document.getElementById('gameFormStatus');
  const gamesTable = document.getElementById('gamesTable');
  const gamesTableBody = gamesTable.querySelector('tbody');
  const questionsSection = document.getElementById('questionsSection');
  let editingGameId = null;

  function fetchGames() {
    fetch('/api/survivor/games')
      .then(res => res.json())
      .then(data => {
        renderGames(data || []);
      });
  }

  function renderGames(games) {
    gamesTableBody.innerHTML = '';
    if (!games.length) {
      gamesTable.style.display = 'none';
      return;
    }
    gamesTable.style.display = '';
    games.forEach(game => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${game.name}</td>
        <td>${game.status}</td>
        <td>${game.isNew ? 'Yes' : 'No'}</td>
        <td>
          <button onclick="editGame('${game.id}')">Edit</button>
          <button class="remove-event-btn" onclick="deleteGame('${game.id}')">Remove</button>
          <button onclick="showQuestions('${game.id}')">Questions</button>
        </td>
      `;
      gamesTableBody.appendChild(tr);
    });
  }

  gameForm.onsubmit = function(e) {
    e.preventDefault();
    gameFormStatus.textContent = '';
    const data = {
      name: gameName.value,
      status: gameStatus.value,
      isNew: gameIsNew.checked
    };
    if (editingGameId) {
      // Update game
      fetch(`/api/survivor/games/${editingGameId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(res => res.json())
        .then(result => {
          gameFormStatus.textContent = 'Game updated!';
          gameForm.reset();
          editingGameId = null;
          fetchGames();
        });
    } else {
      // Add game
      fetch('/api/survivor/games', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(res => res.json())
        .then(result => {
          gameFormStatus.textContent = 'Game added!';
          gameForm.reset();
          fetchGames();
        });
    }
  };

  window.editGame = function(id) {
    fetch('/api/survivor/games')
      .then(res => res.json())
      .then(games => {
        const game = (games || []).find(g => g.id === id);
        if (!game) return;
        gameName.value = game.name;
        gameStatus.value = game.status;
        gameIsNew.checked = !!game.isNew;
        editingGameId = id;
        gameFormStatus.textContent = 'Editing game...';
      });
  };

  window.deleteGame = function(id) {
    if (!confirm('Delete this game?')) return;
    fetch(`/api/survivor/games/${id}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(result => {
        fetchGames();
      });
  };

  window.showQuestions = function(gameId) {
    fetch('/api/survivor/games')
      .then(res => res.json())
      .then(games => {
        const game = (games || []).find(g => g.id === gameId);
        if (!game) return;
        renderQuestions(game);
      });
  };

  function renderQuestions(game) {
    questionsSection.innerHTML = '';
    const h = document.createElement('h4');
    h.textContent = `Questions for ${game.name}`;
    questionsSection.appendChild(h);
    // Add question form
    const form = document.createElement('form');
    form.id = 'questionForm';
    form.innerHTML = `
      <input id="questionText" placeholder="Question Text" required />
      <input id="questionPoints" type="number" placeholder="Points" required />
      <input id="questionChoices" placeholder="Choices (comma separated)" required />
      <input id="questionCorrect" placeholder="Correct Answer" required />
      <button type="submit">Add Question</button>
      <span id="questionFormStatus" style="margin-left:10px;"></span>
    `;
    questionsSection.appendChild(form);
    // Questions table
    const table = document.createElement('table');
    table.style.width = '100%';
    table.innerHTML = '<thead><tr><th>Text</th><th>Points</th><th>Choices</th><th>Correct</th><th>Actions</th></tr></thead>';
    const tbody = document.createElement('tbody');
    (game.questions || []).forEach(q => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${q.text}</td>
        <td>${q.points}</td>
        <td>${q.choices.join(', ')}</td>
        <td>${q.correctAnswer || ''}</td>
        <td>
          <button onclick="editQuestion('${game.id}','${q.id}')">Edit</button>
          <button onclick="deleteQuestion('${game.id}','${q.id}')">Delete</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    questionsSection.appendChild(table);
    // Add question submit
    form.onsubmit = function(e) {
      e.preventDefault();
      const data = {
        text: document.getElementById('questionText').value,
        points: parseInt(document.getElementById('questionPoints').value, 10) || 0,
        choices: document.getElementById('questionChoices').value.split(',').map(s => s.trim()),
        correctAnswer: document.getElementById('questionCorrect').value
      };
      fetch(`/api/survivor/games/${game.id}/questions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(res => res.json())
        .then(result => {
          form.reset();
          renderQuestions({ ...game, questions: [...(game.questions || []), result] });
          fetchGames();
        });
    };
  }

  window.editQuestion = function(gameId, qid) {
    fetch('/api/survivor/games')
      .then(res => res.json())
      .then(games => {
        const game = (games || []).find(g => g.id === gameId);
        if (!game) return;
        const q = (game.questions || []).find(q => q.id === qid);
        if (!q) return;
        renderQuestionsEdit(game, q);
      });
  };

  function renderQuestionsEdit(game, q) {
    questionsSection.innerHTML = '';
    const h = document.createElement('h4');
    h.textContent = `Edit Question for ${game.name}`;
    questionsSection.appendChild(h);
    // Edit question form
    const form = document.createElement('form');
    form.id = 'questionForm';
    form.innerHTML = `
      <input id="questionText" value="${q.text}" placeholder="Question Text" required />
      <input id="questionPoints" type="number" value="${q.points}" placeholder="Points" required />
      <input id="questionChoices" value="${q.choices.join(', ')}" placeholder="Choices (comma separated)" required />
      <input id="questionCorrect" value="${q.correctAnswer || ''}" placeholder="Correct Answer" required />
      <button type="submit">Save Question</button>
      <button type="button" id="cancelEditQ">Cancel</button>
      <span id="questionFormStatus" style="margin-left:10px;"></span>
    `;
    questionsSection.appendChild(form);
    // Questions table
    const table = document.createElement('table');
    table.style.width = '100%';
    table.innerHTML = '<thead><tr><th>Text</th><th>Points</th><th>Choices</th><th>Correct</th><th>Actions</th></tr></thead>';
    const tbody = document.createElement('tbody');
    (game.questions || []).forEach(qq => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${qq.text}</td>
        <td>${qq.points}</td>
        <td>${qq.choices.join(', ')}</td>
        <td>${qq.correctAnswer || ''}</td>
        <td>
          <button onclick="editQuestion('${game.id}','${qq.id}')">Edit</button>
          <button onclick="deleteQuestion('${game.id}','${qq.id}')">Delete</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    questionsSection.appendChild(table);
    // Edit question submit
    form.onsubmit = function(e) {
      e.preventDefault();
      const data = {
        text: document.getElementById('questionText').value,
        points: parseInt(document.getElementById('questionPoints').value, 10) || 0,
        choices: document.getElementById('questionChoices').value.split(',').map(s => s.trim()),
        correctAnswer: document.getElementById('questionCorrect').value
      };
      fetch(`/api/survivor/games/${game.id}/questions/${q.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
        .then(res => res.json())
        .then(result => {
          fetchGames();
          showQuestions(game.id);
        });
    };
    document.getElementById('cancelEditQ').onclick = function() {
      showQuestions(game.id);
    };
  }

  window.deleteQuestion = function(gameId, qid) {
    if (!confirm('Delete this question?')) return;
    fetch(`/api/survivor/games/${gameId}/questions/${qid}`, { method: 'DELETE' })
      .then(res => res.json())
      .then(result => {
        fetchGames();
        showQuestions(gameId);
      });
  };

  fetchGames();

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
    outfitsTable.style.display = '';
    if (!outfits.length) {
      // No outfits, just show empty tbody
      return;
    }
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