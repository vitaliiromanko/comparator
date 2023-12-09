function getFileName() {
    document.getElementById('output').innerText = "";
    let ele = document.getElementById('entry_value');

    let resultList = document.getElementById('resultList');
    if (resultList !== null) {
        resultList.remove();
    }

    let result = ele.files;
    for (let x = 0; x < result.length; x++) {
        let fle = result[x];
        let li = document.createElement('li');
        li.classList.add('list-group-item');
        li.classList.add('d-flex');
        li.classList.add('justify-content-between');
        li.classList.add('align-items-center');
        li.append(fle.name);
        let span = document.createElement('span');
        span.append((fle.size / 1024).toFixed(2) + 'КБ')
        li.append(span);
        document.getElementById('output').append(li);
    }
}