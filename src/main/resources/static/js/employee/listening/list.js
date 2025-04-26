document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('loadingSpinner').classList.remove('d-none');

    const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
    const pathParts = window.location.pathname.split('/');
    let pageNumber = parseInt(pathParts[pathParts.length - 1]); // pageNumber nằm ở cuối URL
    fetch(`${baseUrl}/api/listening_lesson/list/` + pageNumber, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem("token")
        }
    })
        .then(response => handleApiResponse(response, (data) => {
            const tbody = document.querySelector("table tbody");
            tbody.innerHTML = ""; // Xóa dữ liệu cũ nếu có

            data.data.forEach((lesson, index) => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <th scope="row">${index + 1}</th>
                    <td>${lesson.title}</td>
                    <td><a href="${lesson.youtubeUrl}" target="_blank">${lesson.youtubeUrl}</a></td>
                    <td>
                        <span class="${lesson.status === 'done' ? 'badge bg-success' : (lesson.status === 'processing' ? 'badge bg-warning' : 'badge bg-danger')}">
                            ${lesson.status}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="viewLesson(${lesson.id})">Detail</button>
                    </td>
                `;

                tbody.appendChild(row);
            });
            // Handling pagination
            generatePagination(data.totalPages, data.currentPage);

        }))
        .catch(error => {
            console.error("Error fetching lessons:", error);
            alert("Error loading lessons.");
        });
});

// Function to generate pagination links
function generatePagination(totalPages, currentPage) {
    const paginationList = document.getElementById("pagination-list");
    paginationList.innerHTML = ""; // Clear existing pagination

    // Previous button
    const prevButton = document.createElement("li");
    prevButton.classList.add("datatable-pagination-list-item");
    if (currentPage === 0) {
        prevButton.classList.add("datatable-disabled");
    }
    prevButton.innerHTML = `
        <button data-page="${currentPage - 1}" class="datatable-pagination-list-item-link" aria-label="Previous">
            ‹
        </button>
    `;
    paginationList.appendChild(prevButton);

    // Page buttons
    for (let i = 0; i < totalPages; i++) {
        const pageItem = document.createElement("li");
        pageItem.classList.add("datatable-pagination-list-item");
        if (i === currentPage) {
            pageItem.classList.add("datatable-active");
        }
        pageItem.innerHTML = `
            <button data-page="${i}" class="datatable-pagination-list-item-link" aria-label="Page ${i + 1}">
                ${i + 1}
            </button>
        `;
        paginationList.appendChild(pageItem);
    }

    // Next button
    const nextButton = document.createElement("li");
    nextButton.classList.add("datatable-pagination-list-item");
    if (currentPage === totalPages - 1) {
        nextButton.classList.add("datatable-disabled");
    }
    nextButton.innerHTML = `
        <button data-page="${currentPage + 1}" class="datatable-pagination-list-item-link" aria-label="Next">
            ›
        </button>
    `;
    paginationList.appendChild(nextButton);

    // Add event listeners for page navigation
    const pageButtons = paginationList.querySelectorAll("button[data-page]");
    pageButtons.forEach(button => {
        button.addEventListener("click", (e) => {
            const page = parseInt(e.target.getAttribute("data-page"));
            if (page >= 0 && page < totalPages) {
                // Cập nhật URL để điều hướng tới đúng trang
                window.location.href = "/view/admin/listening/list/" + (page + 1); // +1 vì trang bắt đầu từ 1 thay vì 0
            }
        });
    });
}


function viewLesson(id) {
    window.location.href = "/view/admin/listening/detail/" + id;
}
