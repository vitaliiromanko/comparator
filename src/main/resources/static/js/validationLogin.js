let form = document.getElementById('loginForm');
let inputUsernameLogin = document.getElementById('usernameLogin');
let inputPasswordLogin = document.getElementById('passwordLogin');

let usernameErrorLogin = document.getElementById('usernameErrorLogin');
let passwordErrorLogin = document.getElementById('passwordErrorLogin');
let recaptchaError = document.getElementById('recaptchaError');

let validInputUsernameLogin = validUsername(inputUsernameLogin.value);
let validInputPasswordLogin = validPassword(inputPasswordLogin.value);

let usernameErrorLoginMessage = 'Ви некоректно ввели логін!';
let passwordErrorLoginMessage = 'Ви некоректно ввели пароль!';


inputUsernameLogin.addEventListener('blur', function(e) {
    validInputUsernameLogin = addInputListenerLogic(inputUsernameLogin, usernameErrorLogin, validUsername, usernameErrorLoginMessage);
});

inputPasswordLogin.addEventListener('blur', function(e) {
    validInputPasswordLogin = addInputListenerLogic(inputPasswordLogin, passwordErrorLogin, validPassword, passwordErrorLoginMessage);
});


form.addEventListener('submit', function (e) {
    e.preventDefault();
    let validInputs = 0;

    if (checkValid(validInputUsernameLogin, inputUsernameLogin, usernameErrorLogin, usernameErrorLoginMessage)) {
        validInputs++;
    }

    if (checkValid(validInputPasswordLogin, inputPasswordLogin, passwordErrorLogin, passwordErrorLoginMessage)) {
        validInputs++;
    }

    var response = grecaptcha.getResponse();
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

function verifyCaptcha() {
    recaptchaError.innerHTML = ""
}

function checkValid(valid, input, error, message) {
    if (!valid) {
        removeFirstTag(input, error);
        addMessageTag(input, error, message);
        return false;
    }
    return true;
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

    if (str.length < 4) {
        return false;
    }

    return true;
}

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

    if (str.length < 2 || str.length > 30) {
        return false;
    }

    return true;
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
    if (s === ' ') {
        return true;
    } else {
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