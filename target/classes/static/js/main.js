// Modal functionality
window.openModal = function(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
};

window.closeModal = function(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = '';
    }
};

// AJAX profile loader
window.verPerfil = function(id) {
    window.openModal('perfilModal');
    const modalBody = document.getElementById('perfilModalBody');
    if (modalBody) {
        modalBody.innerHTML = '<div style="text-align:center;padding:3rem;"><i class="fas fa-spinner fa-spin fa-3x" style="color:var(--primary);"></i><p style="margin-top:1rem;color:var(--gray);">Cargando perfil...</p></div>';
        fetch('/coordinador/alumnos/perfil-fragment/' + id)
            .then(response => {
                if (!response.ok) throw new Error('Error al cargar');
                return response.text();
            })
            .then(html => {
                modalBody.innerHTML = html;
                // Trigger animations
                setTimeout(() => {
                    modalBody.querySelectorAll('.competency-fill').forEach(bar => {
                        const width = bar.dataset.width;
                        if (width) bar.style.width = width + '%';
                    });
                }, 50);
            })
            .catch(err => {
                modalBody.innerHTML = '<div class="alert alert-danger" style="margin:2rem;"><i class="fas fa-exclamation-circle"></i> Error al cargar el perfil del estudiante.</div>';
            });
    }
};

document.addEventListener('DOMContentLoaded', function() {
    // Mobile sidebar toggle
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');
    const sidebarOverlay = document.getElementById('sidebarOverlay');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('open');
            if (sidebarOverlay) sidebarOverlay.classList.toggle('show');
        });
    }

    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function() {
            sidebar.classList.remove('open');
            sidebarOverlay.classList.remove('show');
        });
    }

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert.fade-in');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            alert.style.transition = 'all 0.3s ease';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Confirm delete actions
    const deleteButtons = document.querySelectorAll('.btn-delete-confirm');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            if (!confirm('¿Esta seguro de que desea eliminar este registro?')) {
                e.preventDefault();
            }
        });
    });



    // Close modal on overlay click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', function(e) {
            if (e.target === this) {
                this.style.display = 'none';
                document.body.style.overflow = '';
            }
        });
    });

    // File upload preview
    const fileInputs = document.querySelectorAll('.file-upload-input');
    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            const fileName = this.files[0]?.name || 'Ningun archivo seleccionado';
            const preview = document.getElementById(this.dataset.preview);
            if (preview) preview.textContent = fileName;
        });
    });

    // Tab navigation
    document.querySelectorAll('.tab-nav button').forEach(tab => {
        tab.addEventListener('click', function() {
            const tabGroup = this.closest('.tab-nav');
            const tabContainer = tabGroup.nextElementSibling || tabGroup.parentElement;
            
            tabGroup.querySelectorAll('button').forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            const tabId = this.dataset.tab;
            if (tabContainer) {
                tabContainer.querySelectorAll('.tab-panel').forEach(panel => {
                    panel.style.display = panel.id === tabId ? 'block' : 'none';
                });
            }
        });
    });

    // Competency bar animations
    setTimeout(() => {
        document.querySelectorAll('.competency-fill').forEach(bar => {
            const width = bar.dataset.width;
            if (width) bar.style.width = width + '%';
        });
    }, 300);

    // Set active state on sidebar based on current URL
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-menu a').forEach(link => {
        if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href'))) {
            link.classList.add('active');
            // If it's the exact match, break out of loop or ensure no other is active
            if (currentPath === link.getAttribute('href')) {
                // Exact match takes precedence
            }
        }
    });
});
