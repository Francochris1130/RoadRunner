let entries = [];
let watchId;
let positions = [];
let totalDistance = 0;

const entriesWrapper = document.querySelector("#entries");
const totalElement = document.getElementById("total");
const averageElement = document.getElementById("average");
const highElement = document.getElementById("high");
const progressTotalElement = document.getElementById("progressTotal");
const targetElement = document.getElementById("target");
const form = document.querySelector("form");
const entryInput = document.querySelector("#entry");
const progressCircle = document.querySelector("#progressCircle");

// Function to add a new entry
function addNewEntry(newEntry) {
    const listItem = document.createElement("li");
    listItem.textContent = `Day ${entries.length}: ${newEntry.toFixed(1)} miles`;
    
    if (entriesWrapper.children.length >= 7) {
        entriesWrapper.removeChild(entriesWrapper.firstElementChild);
    }
    entriesWrapper.appendChild(listItem);
}

// Function to calculate total miles
function calcTotal() {
    const total = entries.reduce((sum, val) => sum + val, 0).toFixed(1);
    totalElement.innerText = total;
    progressTotalElement.innerText = total;
}

// Function to calculate average miles
function calcAverage() {
    if (entries.length === 0) {
        averageElement.innerText = "0.0";
        return;
    }
    const average = (entries.reduce((sum, val) => sum + val, 0) / entries.length).toFixed(1);
    averageElement.innerText = average;
}

// Function to calculate weekly progress percentage
function calcGoal() {
    const total = entries.reduce((sum, val) => sum + val, 0);
    const target = Number(targetElement.innerText) || 25;
    const completedPercent = Math.min(((total / target) * 100).toFixed(1), 100);
    progressCircle.style.background = `conic-gradient(#70db70 ${completedPercent}%, #2d7340 ${completedPercent}% 100%)`;
}

// Function to find the highest mileage entry
function weeklyHigh() {
    if (entries.length === 0) {
        highElement.innerText = "0.0";
        return;
    }
    const high = Math.max(...entries).toFixed(1);
    highElement.innerText = high;
}

// Function to handle form submission
function handleSubmit(event) {
    event.preventDefault();
    
    const entry = parseFloat(entryInput.value);
    if (isNaN(entry) || entry <= 0) return;
    
    entries.push(entry);
    form.reset();
    addNewEntry(entry);
    calcTotal();
    calcAverage();
    weeklyHigh();
    calcGoal();
}

form.addEventListener("submit", handleSubmit);

// Start tracking position
function startTracking() {
    watchId = navigator.geolocation.watchPosition(position => {
        const { latitude, longitude } = position.coords;
        const newPosition = { lat: latitude, lon: longitude, timestamp: Date.now() };
        
        if (positions.length > 0) {
            const lastPosition = positions[positions.length - 1];
            const distance = calculateDistance(lastPosition, newPosition);
            totalDistance += distance;
            totalElement.innerText = totalDistance.toFixed(2) + " miles";
        }
        
        positions.push(newPosition);
        updateMap(latitude, longitude);
        sendDataToAPI(newPosition, totalDistance);
    }, handleError, { enableHighAccuracy: true, maximumAge: 10000, timeout: 10000 });
}

// Stop tracking
function stopTracking() {
    navigator.geolocation.clearWatch(watchId);
    console.log("Stopped tracking.");
}

document.querySelector("button").addEventListener("click", stopTracking);
startTracking();

// Calculate distance between two points (Haversine formula)
function calculateDistance(pos1, pos2) {
    const R = 3958.8;
    const dLat = (pos2.lat - pos1.lat) * (Math.PI / 180);
    const dLon = (pos2.lon - pos1.lon) * (Math.PI / 180);
    const a = Math.sin(dLat / 2) ** 2 + Math.cos(pos1.lat * (Math.PI / 180)) * Math.cos(pos2.lat * (Math.PI / 180)) * Math.sin(dLon / 2) ** 2;
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

// Send data to API
function sendDataToAPI(position, distance) {
    fetch("http://localhost:5000/api/log_run", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ position, distance })
    })
    .then(response => response.json())
    .then(data => console.log("Server response:", data))
    .catch(error => console.error("API error:", error));
}

// Update map
function updateMap(lat, lon) {
    document.getElementById('map').innerHTML = `<iframe width="800" height="300" src="https://maps.google.com/maps?q=${lat},${lon}&z=15&output=embed"></iframe>`;
}
