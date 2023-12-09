$(document).ready(function () {
    $('#historyFilesList').dataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.12.1/i18n/uk.json'
        },
        columnDefs: [
            {
                width: 100,
                targets: [-1, -2]
            },
            {
                orderable: false,
                targets: [-1, -2, -4, -5, -6]
            }
        ],
        sPaginationType: 'full_numbers'
    });
});

function submitSendEmailForm(formId) {
    if (confirm("Ви впевнені, що хочете отримати дані про вибране порівняння на пошту?")) {
        document.getElementById(formId).submit();
    }
}

function submitDownloadFileForm(formId) {
    document.getElementById(formId).submit();
}