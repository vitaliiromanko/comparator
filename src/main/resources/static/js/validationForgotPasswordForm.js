let form = document.getElementById('forgotPasswordForm');

let inputUsername = document.getElementById('username');
let inputEmail = document.getElementById('email');

let usernameError = document.getElementById('usernameError');
let emailError = document.getElementById('emailError');

let validInputUsername = validUsername(inputUsername.value);
let validInputEmail = validEmail(inputEmail.value);

let usernameErrorMessage = 'Ви некоректно ввели логін!';
let emailErrorMessage = 'Електронна пошта вказана некоректно!';


inputUsername.addEventListener('blur', function (e) {
    validInputUsername = addInputListenerLogic(inputUsername, usernameError, validUsername, usernameErrorMessage);
});

inputEmail.addEventListener('blur', function (e) {
    validInputEmail = addInputListenerLogic(inputEmail, emailError, validEmail, emailErrorMessage);
});


form.addEventListener('submit', function (e) {
    e.preventDefault();
    let validInputs = 0;

    if (checkValid(validInputUsername, inputUsername, usernameError, usernameErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validInputEmail, inputEmail, emailError, emailErrorMessage)) {
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

    if (validInputs === 3) {
        form.submit();
    }
    validInputs = 0;
});


function validUsername(str) {
    if (isEmptyField(str)) {
        return false;
    }

    if (hasSpacesBeforeAfterValue(str)) {
        return false;
    }

    for (let i = 0; i < str.length; i++) {
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
function isSpace(s) {
    return s === ' ';
}

function validEmail(str) {
    const regex_pattern = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regex_pattern.test(str);
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