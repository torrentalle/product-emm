	
function same_ca(f) {
  if(f.ca_same.checked == true) {
    f.ra_country.value=f.ca_country.value;
    f.ra_state.value=f.ca_state.value;
    f.ra_loc.value=f.ca_loc.value;
    f.ra_org.value=f.ca_org.value;
    f.ra_org_unit.value=f.ca_org_unit.value;
    f.ra_cn.value=f.ca_cn.value;
    f.ra_exp.value=f.ca_exp.value;
    
    
  }
  else{
	f.ra_country.value="";
        f.ra_state.value="";
        f.ra_loc.value="";
        f.ra_org.value="";
        f.ra_org_unit.value="";
        f.ra_cn.value="";
        f.ra_exp.value="";
  }
}

function same_ra(f) {
  if(f.ra_same.checked == true) {
    f.ssl_country.value=f.ra_country.value;
    f.ssl_state.value=f.ra_state.value;
    f.ssl_loc.value=f.ra_loc.value;
    f.ssl_org.value=f.ra_org.value;
    f.ssl_org_unit.value=f.ra_org_unit.value;
    f.ssl_exp.value=f.ra_exp.value;
  }
  else{
	f.ssl_country.value="";
        f.ssl_state.value="";
        f.ssl_loc.value="";
        f.ssl_org.value="";
        f.ssl_org_unit.value="";
        f.ssl_exp.value="";
  }
}

function ValidateIPaddress(inputText)  
{  
  var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
  var re = new RegExp(/^(?!:\/\/)([a-zA-Z0-9]+\.)?[a-zA-Z0-9][a-zA-Z0-9-]+\.[a-zA-Z]{2,6}?$/);
  if(inputText.value.match(ipformat)||inputText.value.match(re))  
  {  
    //document.form1.text1.focus();  
    return false;  
  }  
  else  
  {  
 
 
    return true;  
  }  
}

function NotSameInput(inputText,sameInput)
{
 if(inputText.localeCompare(sameInput)==0)
 {
  
  return true;
 }
 else{
  return false;
 }
}

