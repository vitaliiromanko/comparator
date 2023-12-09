let form = document.getElementById('sendToUsForm');

let inputFirstName = document.getElementById('firstName');
let inputLastName = document.getElementById('lastName');
let textareaSendMessageBody = document.getElementById('sendMessageBody');

let firstNameError = document.getElementById('firstNameError');
let lastNameError = document.getElementById('lastNameError');
let messageError = document.getElementById('sendMessageBodyError');

let validFirstName = validName(inputFirstName.value);
let validLastName = validName(inputLastName.value);
let validSendMessageBody = validMessage(textareaSendMessageBody.value);

let firstNameErrorMessage = 'Ви некоректно ввели ім\'я!';
let lastNameErrorMessage = 'Ви некоректно ввели прізвище!';
let messageErrorMessage = 'Ви не ввели повідомлення!';


inputFirstName.addEventListener('blur', function (e) {
    validFirstName = addInputListenerLogic(inputFirstName, firstNameError, validName, firstNameErrorMessage);
});

inputLastName.addEventListener('blur', function (e) {
    validLastName = addInputListenerLogic(inputLastName, lastNameError, validName, lastNameErrorMessage);
});

textareaSendMessageBody.addEventListener('blur', function (e) {
    validSendMessageBody = addInputListenerLogic(textareaSendMessageBody, messageError, validMessage, messageErrorMessage);
});


form.addEventListener('submit', function (e) {
    e.preventDefault();
    let validInputs = 0;

    if (checkValid(validFirstName, inputFirstName, firstNameError, firstNameErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validLastName, inputLastName, lastNameError, lastNameErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validSendMessageBody, textareaSendMessageBody, messageError, messageErrorMessage)) {
        validInputs++;
    }

    let response = grecaptcha.getResponse();
    if (response.length === 0) {
        if (recaptchaError.children.length === 0) {
            recaptchaError.innerHTML = 'Ви не пройшли перевірку!'
        }
    } else {
        validInputs++;
    }

    if (validInputs === 4) {
        form.submit();
    }
    validInputs = 0;
});


function validMessage(str) {
    return !isEmptyField(str);

}

function validName(str) {
    if (isEmptyField(str)) {
        return false;
    }

    if (hasSpacesBeforeAfterValue(str)) {
        return false;
    }

    for (let i = 0; i < str.length; i++) {
        if (isNumeric(str[i])) {
            return false;
        }
        if (isSpace(str[i])) {
            return false;
        }
    }

    return !(str.length < 2 || str.length > 30);
}

function isEmptyField(str) {
    return str.length === 0 || hasOnlySpaces(str);
}
function hasOnlySpaces(str) {
    return str.trim().length === 0;
}
function hasSpacesBeforeAfterValue(str) {
    return str.length > str.trim().length;
}
function isNumeric(n) {
    if (n === ' ') {
        return false;
    }
    return !isNaN(n);
}
function isSpace(s) {
    return s === ' ';
}

function checkValid(valid, input, error, message) {
    if (!valid) {
        removeFirstTag(input, error);
        addMessageTag(input, error, message);
        return false;
    }
    return true;
}

function addInputListenerLogic(input, error, func, message) {
    removeFirstTag(input, error);
    let inputValue = input.value;
    if (func(inputValue)) {
        return true;
    } else {
        addMessageTag(input, error, message);
        return false;
    }
}

function removeFirstTag(input, item) {
    while (item.firstChild) {
        item.removeChild(item.firstChild);
    }
    input.classList.remove("is-invalid");
}

function addMessageTag(input, item, message) {
    item.innerHTML = message;
    input.classList.add("is-invalid");
}