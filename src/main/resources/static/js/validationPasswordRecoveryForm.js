let form = document.getElementById('passwordRecoveryForm');

let inputPassword = document.getElementById('password');
let inputPassword2 = document.getElementById('password2');

let passwordError = document.getElementById('passwordError');
let password2Error = document.getElementById('password2Error');

let validInputPassword = validPassword(inputPassword.value);
let validInputPassword2 = validPassword2(inputPassword2.value);

let passwordErrorMessage = 'Ви некоректно ввели пароль!';
let password2ErrorMessage = 'Введіть пароль повторно!';


inputPassword.addEventListener('blur', function (e) {
    validInputPassword = addInputListenerLogic(inputPassword, passwordError, validPassword, passwordErrorMessage);
});

inputPassword2.addEventListener('blur', function (e) {
    validInputPassword2 = addInputListenerLogic(inputPassword2, password2Error, validPassword2, password2ErrorMessage);
});


form.addEventListener('submit', function (e) {
    e.preventDefault();
    let validInputs = 0;

    if (checkValid(validInputPassword, inputPassword, passwordError, passwordErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validInputPassword2, inputPassword2, password2Error, password2ErrorMessage)) {
        if (inputPassword.value === inputPassword2.value) {
            validInputs++;
        } else {
            validInputPassword2 = false;
            checkValid(validInputPassword2, inputPassword2, password2Error, password2ErrorMessage);
        }
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


function validPassword2(str) {
    return inputPassword.value === str && validPassword(str);
}

function validPassword(str) {
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
    return str.length >= 4;
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
