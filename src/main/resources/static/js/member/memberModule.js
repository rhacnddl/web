const signupForm = document.querySelector('#form-signup');

const validations = {
    email: false,
    password: false,
    nickname: false,
    phone: false,
    address: false,
};

let emailAuth = null;

const memberModule = {
    init: function() {
        //declare this object
        const _this = this;

        //select elements of the form for event
        const email = document.getElementById('signup-email');
        const password = document.getElementById('signup-password');
        const passwordConfirmed = document.getElementById('passwordConfirmed');
        const nickname = document.getElementById('nickname');
        const phone = document.getElementById('phone');
        const addressFinder = document.getElementById('addr');
        const submitButton = document.getElementById('submitBtn');

        //password check
        passwordConfirmed.addEventListener('blur', () => {
            validations.password = passwordConfirmed.value === password.value ? true : false;
        });

        //nickname check
        nickname.addEventListener('blur', (e) => {
            console.log(e.target.value);
            _this.isOverlapped(e.target.value);
        });

        // auto-hyphen in between phone numbers
        const autoHyphenPhone = (str) => {
            str = str.replace(/[^0-9]/g, '');
            let tmp = '';
            if (str.length < 4) {
                return str;
            } else if (str.length < 7) {
                tmp += str.substr(0, 3);
                tmp += '-';
                tmp += str.substr(3);
                return tmp;
            } else if (str.length < 11) {
                tmp += str.substr(0, 3);
                tmp += '-';
                tmp += str.substr(3, 3);
                tmp += '-';
                tmp += str.substr(6);
                return tmp;
            } else {
                tmp += str.substr(0, 3);
                tmp += '-';
                tmp += str.substr(3, 4);
                tmp += '-';
                tmp += str.substr(7);
                validations.phone = true;
                return tmp;
            }
            return str;
        }
        //phone number keyup event for auth-hyphen function
        phone.addEventListener('keyup', () => {
            phone.value = autoHyphenPhone(phone.value);
        });

        //address api
        addressFinder.addEventListener('click', () => {
            const platformType = navigator.userAgent.indexOf('Mob') === -1 ? 'P' : 'M';
            window.open(`/member/getAddrInfo?platformType=${platformType}`,'pop','width=570,height=420, scrollbars=yes, resizable=yes');
        });

        // validation on submit
        submitButton.addEventListener('click', async () => {
            if(!emailAuth) return alert('이메일 인증이 필요합니다.');

            const csrfToken = document.querySelector('input[name=_csrf]').value;
            const url = '/emailAuth/confirmCheck';
            console.log('url: ', url);
            console.log(emailAuth);
            if(!agreedWithLocation) return alert('위치정보수집 동의가 필요합니다.');
            if (emailAuth.confirmed) {
                alert('회원가입이 완료되었습니다.');
                signupForm.submit();
                return;
            }

            const options = {
                method: 'POST',
                body: JSON.stringify(emailAuth),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Accept': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                }
            };
            //email check
            let result = await fetch(url, options)
                .then(res => res.json())
                .catch(err => console.log(err));
            console.log(result);
            if (result) {
                emailAuth = result;
                if (!emailAuth.confirmed) return alert('발송된 인증메일을 확인해주세요.');
                validations.email = true;

                const isValid = Object.values(validations).every(val => {
                    return val === true;
                });

                if(!isValid) return alert('입력하신 정보를 확인해주세요');
                if(!agreedWithLocation) return alert('위치정보수집 동의가 필요합니다.');
            }
        }); // end validation
    }, // end init

    isOverlapped : function(value) {
        const url = `/member/isOverlapped/nicknames/${value}`;
        console.log(url);
        const options = {
            method: 'GET',
        };
        fetch(url, options)
            .then(res => res.json())
            .then(data => {
                console.log(data);
                if(data > 0) {
                    validations.nickname = false;
                    return alert('중복된 닉네임입니다.');
                } else {
                    validations.nickname = true;
                }
            })
            .catch(err => console.error(err));
    } // end isOverlapped

}; // end module object

memberModule.init();
//address callback function
// function putAddr(roadAddrPart1, addrDetail){
//     document.getElementById('addr').value = roadAddrPart1;
//     document.getElementById('addrDtl').value = addrDetail;
//     validations.address = true;
// };
