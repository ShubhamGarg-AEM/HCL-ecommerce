	const findRedriectUrl = function(domId) {
		let redirectURL = "";
		const params = new URLSearchParams(window.location.search);
		const keys = ['signin.html', 'signup.html'];
		if(params.has('referer')) {
			if(!keys.some(k => params.get('referer').includes(k) )) {
				redirectURL = params.get('referer');
			}
		} else if(domId.getAttribute("data-default")) {
			redirectURL = domId.getAttribute("data-default");
		}
		return redirectURL;
	}

const handleHttpServerRequestJson = function (url, formdata) {
			loader(true);
            var othrParm = {
              headers: {"content-type":"application/json; charset=UTF-8", 'Accept': 'application/json'},
              body: JSON.stringify(formdata),
              method: "POST"
            }
            fetch('/libs/granite/csrf/token.json')
            .then(
                (response) => {return response.json();},
                (rejected) => {console.log(rejected);
            })
            .then( msg => {
                    othrParm.headers['CSRF-Token'] = msg.token;
                        return fetch(url, othrParm);
            })
            .then(
                (response) => {return response.json();},
                (rejected) => {console.log(rejected);
            })
            .then(data => {
				loader(false);
                const status = (data.status)?JSON.parse(data.status): false;
                if(status === true) {
                  if(checkUserCookie("hcluser") === false){
                    const exdays = (formdata.rememberme)?5:1;
                    setUserCookie("hcluser",JSON.stringify(data.message),exdays);
                  }
                  setTimeout(function(){window.location = findRedriectUrl(document.login_form);}, 1000);
                } else {
                  let error = "Server status failed. ";
                  if(data.message.error) {
                    console.log(data.message.error);
                     error = data.message.error;
                  }
                  console.log(error);
                  const errorElm = document.getElementById('cmp-login-errormsg');
                  errorElm.style.visibility = "visible";
                  errorElm.innerHTML  = '<span>'+ error+ '</span>';
                }
            })
            .catch((error) => {
              console.log('promise error',error);
            });
        }
  
  async function validateLoginFrom(e) {
	const ErrorMsgElm = document.getElementById('cmp-login-errormsg');
	ErrorMsgElm.innerHTML = "";
	ErrorMsgElm.style.visibility = "hidden";
	if(document.login_form.username.value == "") {
	  document.login_form.username.focus();
	  return false;
	}
	if(document.login_form.password.value == "") {
	  document.login_form.password.focus();
	  return false;
	}
	
	const data = {  
		username: document.login_form.username.value,
		password: document.login_form.password.value,
		rememberme: document.login_form.rememberme.checked
	}
	
	let url = '/bin/hclecomm/customerSignin';
	await handleHttpServerRequestJson(url,data);
	
	return true;
  }



