package jb.smsmedia.iparliament.dtos;

public class PostVote {
   String user_id, post_id, vote_type, post_type, number;

public String getUser_id() {
	return user_id;
}

public void setUser_id(String user_id) {
	this.user_id = user_id;
}

public String getPost_id() {
	return post_id;
}

public void setPost_id(String post_id) {
	this.post_id = post_id;
}

public String getVote_type() {
	return vote_type;
}

public void setVote_type(String vote_type) {
	this.vote_type = vote_type;
}

public String getPost_type() {
	return post_type;
}

public void setPost_type(String post_type) {
	this.post_type = post_type;
}

public String getNumber() {
	return number;
}

public void setNumber(String number) {
	this.number = number;
}
   
}
