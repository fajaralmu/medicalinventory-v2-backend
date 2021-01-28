function commonAuthHeader() {
	const header = {};
	if(document.getElementById("jwt_token")) {
		header["Authorization"] = "Bearer "+ document.getElementById("jwt_token").value;
	}
	
	return header;
	
} 