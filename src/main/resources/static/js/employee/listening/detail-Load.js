export var lines = []; // phải để const
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('loadingSpinner').classList.remove('d-none');

    const detailBreadcrumb = document.getElementById("detail-breadcrumb");

    const baseUrl = window.location.origin;
    const pathParts = window.location.pathname.split('/');
    const id = parseInt(pathParts[pathParts.length - 1]);

    // Update breadcrumb href for Detail link only
    if (detailBreadcrumb) {
        detailBreadcrumb.href = `/view/admin/listening/detail/${id}`;
    }

    // Fetch lesson data from API
    fetchLessonData(id)
        .then(data => {
            lines = data.lines;
            setupAudio(data);
            // Ẩn spinner khi nhận phản hồi
            document.getElementById('loadingSpinner').classList.add('d-none');
        })
        .catch(error => {
            // Ẩn spinner khi nhận phản hồi
            document.getElementById('loadingSpinner').classList.add('d-none');
            subtitlesList.innerHTML = '<li class="list-group-item text-danger">Error loading subtitles</li>';
            alert("Error loading lessons.");
        });
});

// Hàm fetch dữ liệu bài học
function fetchLessonData(id) {
    const baseUrl = window.location.origin;
    return fetch(`${baseUrl}/api/listening_lesson/details/${id}`, {
        method: 'GET', headers: {
            'Authorization': localStorage.getItem("token"), 'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(response => {
            if (response.status === "success") {
                return response.data;
            } else {
                throw new Error('API response status not success');
            }
        });
}

// Hàm thiết lập audio
function setupAudio(data) {
    var audioPlayer = document.getElementById("audio-player");
    var audioSource = document.getElementById("audio-source");

    document.querySelector(".card-title").textContent = data.title;
    audioSource.src = data.audioUrl;
    audioPlayer.load();
}
