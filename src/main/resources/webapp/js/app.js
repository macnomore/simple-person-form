const form = document.getElementById("personForm");
const statusEl = document.getElementById("status");

function setStatus(message, kind) {
  statusEl.textContent = message;
  statusEl.classList.remove("ok", "err");
  if (kind) statusEl.classList.add(kind);
}

form.addEventListener("submit", async e => {
  e.preventDefault();

  const firstName = document.getElementById("firstName").value.trim();
  const lastName = document.getElementById("lastName").value.trim();

  setStatus("Saving…");

  try {
    const res = await fetch("/api/person", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ firstName, lastName })
    });

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      setStatus(data.message || `Request failed (${res.status})`, "err");
      return;
    }

    setStatus(`Saved! ID=${data.id} • Full Name: ${data.fullName}`, "ok");
    form.reset();
    document.getElementById("firstName").focus();
  } catch (err) {
    setStatus("Network error. Is the backend running?", "err");
  }
});
