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
  if (!password) {
    // First time: get password from field
    const pw = document.getElementById('password').value;
    if (!pw) {
      document.getElementById('pushStatus').textContent = 'Please enter the admin password.';
      document.getElementById('pushStatus').className = 'error';
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

  // Preset button logic for new HTML
  document.querySelectorAll('.preset-btn').forEach(btn => {
    btn.onclick = function() {
      document.getElementById('notifTitle').value = btn.getAttribute('data-title');
      document.getElementById('notifMessage').value = btn.getAttribute('data-message');
      document.getElementById('notifType').value = btn.getAttribute('data-type');
    };
  });
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
// --- Livestreams Management ---
function renderLivestreams(livestreams) {
  const container = document.getElementById('livestreams');
  container.innerHTML = '';
  livestreams.forEach((ls, idx) => {
    const row = document.createElement('div');
    row.className = 'livestream-row';
    row.innerHTML = `
      <input type="text" placeholder="Title" value="${ls.title || ''}" style="width: 20%; margin-right: 8px;" />
      <input type="text" placeholder="URL" value="${ls.url || ''}" style="width: 40%; margin-right: 8px;" />
      <input type="text" placeholder="Time" value="${ls.time || ''}" style="width: 20%; margin-right: 8px;" />
      <button onclick="this.parentNode.remove();">Delete</button>
    `;
    container.appendChild(row);
  });
}

function loadLivestreams() {
  fetch('/api/livestreams')
    .then(res => res.json())
    .then(data => renderLivestreams(data || []));
}

function addRow() {
  const container = document.getElementById('livestreams');
  const row = document.createElement('div');
  row.className = 'livestream-row';
  row.innerHTML = `
    <input type="text" placeholder="Title" style="width: 20%; margin-right: 8px;" />
    <input type="text" placeholder="URL" style="width: 40%; margin-right: 8px;" />
    <input type="text" placeholder="Time" style="width: 20%; margin-right: 8px;" />
    <button onclick="this.parentNode.remove();">Delete</button>
  `;
  container.appendChild(row);
}

function saveLivestreams() {
  const rows = document.querySelectorAll('#livestreams .livestream-row');
  const data = Array.from(rows).map(row => {
    const inputs = row.querySelectorAll('input');
    return {
      title: inputs[0].value,
      url: inputs[1].value,
      time: inputs[2].value
    };
  }).filter(ls => ls.title || ls.url || ls.time);
  fetch('/api/livestreams', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  }).then(res => {
    if (res.ok) {
      alert('Livestreams saved!');
      loadLivestreams();
    } else {
      alert('Error saving livestreams');
    }
  });
}

window.addEventListener('DOMContentLoaded', function() {
  loadLivestreams();
}); 