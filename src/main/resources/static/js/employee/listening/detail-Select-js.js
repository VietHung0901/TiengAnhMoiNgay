document.addEventListener("DOMContentLoaded", function() {
    const tabButtons = document.querySelectorAll('.nav-link');
    const scripts = {
        "Listen": "/js/employee/listening/detail-Listen.js",
        "Listen-write": "/js/employee/listening/detail-ListenAndWrite.js",
        "Listen-repeat": "/js/employee/listening/detail-ListenAndRepeat.js"
    };

    tabButtons.forEach(button => {
        button.addEventListener("click", function() {
            const target = this.getAttribute("data-bs-target").substring(1); // Lấy ID của tab
            loadScript(scripts[target]);
        });
    });

    function loadScript(src) {
        const existingScript = document.getElementById('dynamic-script');
        if (existingScript) {
            existingScript.remove(); // Gỡ bỏ script cũ nếu có
        }


        const script = document.createElement("script");
        script.type = 'module';
        script.id = 'dynamic-script'; // Thêm ID để dễ dàng quản lý
        script.src = src;
        document.body.appendChild(script);
    }
});