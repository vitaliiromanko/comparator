$(document).ready(function () {
    $('#userListTable').dataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.12.1/i18n/uk.json'
        },
        columnDefs: [
            {
                orderable: false,
                width: 120,
                targets: [-1, -2]
            }
        ],
        sPaginationType: 'full_numbers'
    });
});

function submitDeleteForm(formId, username) {
    if (confirm("Ви впевнені, що хочете видалити користувача " + username + "?")) {
        document.getElementById(formId).submit();
    }
}