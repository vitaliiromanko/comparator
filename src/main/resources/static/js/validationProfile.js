let form = document.getElementById('profileForm');

let inputFirstName = document.getElementById('firstName');
let inputLastName = document.getElementById('lastName');
let inputUsername = document.getElementById('username');
let inputOldPassword = document.getElementById('oldPassword');
let inputNewPassword = document.getElementById('newPassword');

let firstNameError = document.getElementById('firstNameError');
let lastNameError = document.getElementById('lastNameError');
let usernameError = document.getElementById('usernameError');
let oldPasswordError = document.getElementById('oldPasswordError');
let newPasswordError = document.getElementById('newPasswordError');

let validInputFirstName = true;
let validInputLastName = true;
let validInputUsername = true;
let validInputOldPassword = true;
let validInputNewPassword = true;

let firstNameErrorMessage = 'Ви некоректно ввели ім\'я';
let lastNameErrorMessage = 'Ви некоректно ввели прізвище';
let usernameErrorMessage = 'Ви некоректно ввели логін!';
let passwordErrorMessage = 'Ви некоректно ввели пароль!';


inputFirstName.addEventListener('blur', function (e) {
    validInputFirstName = addInputListenerLogic(inputFirstName, firstNameError, validName, firstNameErrorMessage);
});

inputLastName.addEventListener('blur', function (e) {
    validInputLastName = addInputListenerLogic(inputLastName, lastNameError, validName, lastNameErrorMessage);
});

inputUsername.addEventListener('blur', function (e) {
    validInputUsername = addInputListenerLogic(inputUsername, usernameError, validUsername, usernameErrorMessage);
});

inputOldPassword.addEventListener('blur', function (e) {
    validInputOldPassword = addInputListenerLogic(inputOldPassword, oldPasswordError, validPassword, passwordErrorMessage);
});

inputNewPassword.addEventListener('blur', function (e) {
    validInputNewPassword = addInputListenerLogic(inputNewPassword, newPasswordError, validPassword, passwordErrorMessage);
});


form.addEventListener('submit', function (e) {
    e.preventDefault();
    let validInputs = 0;

    if (checkValid(validInputFirstName, inputFirstName, firstNameError, firstNameErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validInputLastName, inputLastName, lastNameError, lastNameErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validInputUsername, inputUsername, usernameError, usernameErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validInputOldPassword, inputOldPassword, oldPasswordError, passwordErrorMessage)) {
        validInputs++;
    }

    if (checkValid(validInputNewPassword, inputNewPassword, newPasswordError, passwordErrorMessage)) {
        validInputs++;
    }

    if (checkValidPasswords(inputOldPassword, inputNewPassword)) {
        validInputs++;
    }


    if (validInputs === 6) {
        form.submit();
    }
    validInputs = 0;
});


function checkValidPasswords(inputOldPassword, inputNewPussword) {
    let oldPassword = inputOldPassword.value;
    let newPassword = inputNewPussword.value;
    if (oldPassword.length === 0 && newPassword.length === 0) {
        return true;
    } else if (oldPassword.length !== 0 && newPassword.length !== 0) {
        return true;
    } else if (oldPassword.length === 0) {
        validInputOldPassword = false;
        checkValid(validInputOldPassword, inputOldPassword, oldPasswordError, "Ви не ввели старого паролю!");
        return false;
    } else {
        validInputNewPassword = false;
        checkValid(validInputNewPassword, inputNewPassword, newPasswordError, "Ви не ввели нового паролю!");
        return false;
    }

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

function checkValid(valid, input, error, message) {
    if (!valid) {
        removeFirstTag(input, error);
        addMessageTag(input, error, message);
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

    return !(str.length < 2 || str.length > 30);


}

function validPassword(str) {
    if (str.length === 0) {
        return true;
    }

    if (hasOnlySpaces(str)) {
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