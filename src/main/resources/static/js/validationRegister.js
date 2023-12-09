let form = document.getElementById('registerForm');

let inputFirstNameRegister = document.getElementById('firstNameRegister');
let inputLastNameRegister = document.getElementById('lastNameRegister');
let inputUsernameRegister = document.getElementById('usernameRegister');
let inputPasswordRegister = document.getElementById('passwordRegister');
let inputPassword2Register = document.getElementById('password2Register');
let inputEmailRegister = document.getElementById('emailRegister');

let firstNameErrorRegister = document.getElementById('firstNameErrorRegister');
let lastNameErrorRegister = document.getElementById('lastNameErrorRegister');
let usernameErrorRegister = document.getElementById('usernameErrorRegister');
let passwordErrorRegister = document.getElementById('passwordErrorRegister');
let password2ErrorRegister = document.getElementById('password2ErrorRegister');
let emailErrorRegister = document.getElementById('emailErrorRegister');
let recaptchaError = document.getElementById('recaptchaError');

let validInputFirstNameRegister = validName(inputFirstNameRegister.value);
let validInputLastNameRegister = validName(inputLastNameRegister.value);
let validInputUsernameRegister = validUsername(inputUsernameRegister.value);
let validInputPasswordRegister = validPassword(inputPasswordRegister.value);
let validInputPassword2Register = validPassword2(inputPassword2Register.value);
let validInputEmailRegister = validEmail(inputEmailRegister.value);



let firstNameErrorRegisterMessage = 'Ви некоректно ввели ім\'я!';
let lastNameErrorRegisterMessage = 'Ви некоректно ввели прізвище!';
let usernameErrorRegisterMessage = 'Ви некоректно ввели логін!';
let passwordErrorRegisterMessage = 'Ви некоректно ввели пароль!';
let password2ErrorRegisterMessage = 'Введіть пароль повторно!';
let emailErrorRegisterMessage = 'Електронна пошта вказана некоректно!';



inputFirstNameRegister.addEventListener('blur', function(e) {
    validInputFirstNameRegister = addInputListenerLogic(inputFirstNameRegister, firstNameErrorRegister, validName, firstNameErrorRegisterMessage);
});

inputLastNameRegister.addEventListener('blur', function(e) {
    validInputLastNameRegister = addInputListenerLogic(inputLastNameRegister, lastNameErrorRegister, validName, lastNameErrorRegisterMessage);
});

inputUsernameRegister.addEventListener('blur', function(e) {
    validInputUsernameRegister = addInputListenerLogic(inputUsernameRegister, usernameErrorRegister, validUsername, usernameErrorRegisterMessage);
});

inputPasswordRegister.addEventListener('blur', function(e) {
    validInputPasswordRegister = addInputListenerLogic(inputPasswordRegister, passwordErrorRegister, validPassword, passwordErrorRegisterMessage);
});

inputPassword2Register.addEventListener('blur', function(e) {
    validInputPassword2Register = addInputListenerLogic(inputPassword2Register, password2ErrorRegister, validPassword2, password2ErrorRegisterMessage);
});

inputEmailRegister.addEventListener('blur', function(e) {
    validInputEmailRegister = addInputListenerLogic(inputEmailRegister, emailErrorRegister, validEmail, emailErrorRegisterMessage);
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


form.addEventListener('submit', function (e) {
    e.preventDefault();
    let validInputs = 0;


    if (checkValid(validInputFirstNameRegister, inputFirstNameRegister, firstNameErrorRegister, firstNameErrorRegisterMessage)) {
        validInputs++;
    }

    if (checkValid(validInputLastNameRegister, inputLastNameRegister, lastNameErrorRegister, lastNameErrorRegisterMessage)) {
        validInputs++;
    }

    if (checkValid(validInputUsernameRegister, inputUsernameRegister, usernameErrorRegister, usernameErrorRegisterMessage)) {
        validInputs++;
    }

    if (checkValid(validInputPasswordRegister, inputPasswordRegister, passwordErrorRegister, passwordErrorRegisterMessage)) {
        validInputs++;
    }

    if (checkValid(validInputPassword2Register, inputPassword2Register, password2ErrorRegister, password2ErrorRegisterMessage)) {
        if (inputPasswordRegister.value === inputPassword2Register.value) {
            validInputs++;
        } else {
            validInputPassword2Register = false;
            checkValid(validInputPassword2Register, inputPassword2Register, password2ErrorRegister, password2ErrorRegisterMessage);
        }
    }

    if (checkValid(validInputEmailRegister, inputEmailRegister, emailErrorRegister, emailErrorRegisterMessage)) {
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


    if (validInputs === 7) {
        form.submit();
    }
    validInputs = 0;
});



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


function validEmail(str) {
    const regex_pattern = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regex_pattern.test(str);
}


function validPassword2(str) {
    return inputPasswordRegister.value === str && validPassword(str);
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
function isNumeric(n) {
    if (n === ' ') {
        return false;
    }
    return !isNaN(n);
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