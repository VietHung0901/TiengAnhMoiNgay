document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('loadingSpinner').classList.remove('d-none');

    const baseUrl = window.location.origin;
    const pathParts = window.location.pathname.split('/');

    const role = pathParts[2]; // "admin" hoặc "user"
    let pageNumber = parseInt(pathParts[pathParts.length - 1]);

    var url = `${baseUrl}/api/listening_lesson/list/` + pageNumber;
    if (role === "user")
        url = url + `?status=done`;
    fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem("token")
        }
    })
        .then(response => handleApiResponse(response, (data) => {
            const tbody = document.querySelector("table tbody");
            tbody.innerHTML = "";

            data.data.forEach((lesson, index) => {

                let youtubeColumn = "";
                let statusColumn = "";
                let detail = "Study";
                if (role !== "user") {
                    if (lesson.youtubeUrl && lesson.youtubeUrl.trim() !== '') {
                        youtubeColumn = `<td><a href="${lesson.youtubeUrl}" target="_blank">Link</a></td>`;
                    } else {
                        youtubeColumn = `<td>Audio</td>`;
                    }
                    statusColumn = `<td>
                                        <span class="${lesson.status === 'done' ? 'badge bg-success' : (lesson.status === 'processing' ? 'badge bg-warning' : 'badge bg-danger')}">
                                            ${lesson.status}
                                        </span>
                                    </td>`;
                    detail = 'Detail';
                }

                const row = document.createElement("tr");
                row.innerHTML = `
                <th scope="row">${index + 1}</th>
                <td>${lesson.title}</td>
                <td>${lesson.level}</td>
                ${youtubeColumn}
                ${statusColumn}
                <td>
                    <button class="btn btn-sm btn-primary" onclick="viewLesson(${lesson.id})">${detail}</button>
                </td>
            `;
                tbody.appendChild(row);
            });

            generatePagination(data.totalPages, data.currentPage, role);
        }))
        .catch(error => {
            console.error("Error fetching lessons:", error);
            alert("Error loading lessons.");
        });
});

function generatePagination(totalPages, currentPage, role) {
    const paginationList = document.getElementById("pagination-list");
    paginationList.innerHTML = "";

    const prevButton = document.createElement("li");
    prevButton.classList.add("datatable-pagination-list-item");
    if (currentPage === 0) prevButton.classList.add("datatable-disabled");
    prevButton.innerHTML = `
        <button data-page="${currentPage - 1}" class="datatable-pagination-list-item-link" aria-label="Previous">
            ‹
        </button>
    `;
    paginationList.appendChild(prevButton);

    for (let i = 0; i < totalPages; i++) {
        const pageItem = document.createElement("li");
        pageItem.classList.add("datatable-pagination-list-item");
        if (i === currentPage) pageItem.classList.add("datatable-active");
        pageItem.innerHTML = `
            <button data-page="${i}" class="datatable-pagination-list-item-link" aria-label="Page ${i + 1}">
                ${i + 1}
            </button>
        `;
        paginationList.appendChild(pageItem);
    }

    const nextButton = document.createElement("li");
    nextButton.classList.add("datatable-pagination-list-item");
    if (currentPage === totalPages - 1) nextButton.classList.add("datatable-disabled");
    nextButton.innerHTML = `
        <button data-page="${currentPage + 1}" class="datatable-pagination-list-item-link" aria-label="Next">
            ›
        </button>
    `;
    paginationList.appendChild(nextButton);

    const pageButtons = paginationList.querySelectorAll("button[data-page]");
    pageButtons.forEach(button => {
        button.addEventListener("click", (e) => {
            const page = parseInt(e.target.getAttribute("data-page"));
            if (page >= 0 && page < totalPages) {
                window.location.href = `/view/${role}/listening/list/` + (page + 1);
            }
        });
    });
}

function viewLesson(id) {
    const role = window.location.pathname.split('/')[2];
    window.location.href = `/view/${role}/listening/detail/${id}`;
}
