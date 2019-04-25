'use strict';

var rootUrl = "/java_s04/api/v1.1/posts";

findAll();

$('#savePost').click(function() {
	var name = $('#name').val();
	if (name === '') {
		$('.error').text('名前は必須入力です。');
		return false;
	} else {
		$('.error').text('');
	}

	var id = $('#postId').val()
	if (id == '')
		addPost();
	else
		updatePost(id);
	return false;
})

$('#newPost').click(function() {
	renderDetails({});
});

function findAll(){
	console.log('findAll start.')
	$.ajax({
		type: "GET",
		url: rootUrl,
		dataType: "json",
		success: renderTable
	});
}

function findById(id) {
	console.log('findByID start - id:'+id);
	$.ajax({
		type: "GET",
		url: rootUrl+'/'+id,
		dataType: "json",
		success: function(data) {
			console.log('findById success: ' + data.name);
			renderDetails(data)
		}
	});
}

function addPost() {
	console.log('addPost start');
	$.ajax({
		type: "POST",
		contentType: "application/json",
		url: rootUrl,
		dataType: "json",
		data: formToJSON(),
		success: function(data, textStatus, jqXHR) {
			alert('部署データの追加に成功しました');
			$('#postId').val(data.id);
			findAll();
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('部署データの追加に失敗しました');
		}
	})
}

function updatePost(id) {
	console.log('updatePost start');
	$.ajax({
		type: "PUT",
		contentType: "application/json",
		url: rootUrl+'/'+id,
		dataType: "json",
		data: formToJSON(),
		success: function(data, textStatus, jqXHR) {
			alert('部署データの更新に成功しました');
			findAll();
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('部署データの更新に失敗しました');
		}
	})
}


function deleteById(id) {
	console.log('delete start - id:'+id);
	$.ajax({
		type: "DELETE",
		url: rootUrl+'/'+id,
		success: function() {
			findAll();
			$('#postId').val('');
			$('#name').val('');
		}
	});
}

function renderTable(data) {
	var headerRow = '<tr><th>ID</th><th>部署名</th></tr>';

	$('#posts').children().remove();

	if (data.length === 0) {
		$('#posts').append('<p>現在データが存在していません。</p>')
	} else {
		var table = $('<table>').attr('border', 1);
		table.append(headerRow);
		$.each(data, function(index, post) {
			var row = $('<tr>');
			row.append($('<td>').text(post.id));
			row.append($('<td>').text(post.name));
			row.append($('<td>').append(
					$('<button>').text("編集").attr("type","button").attr("onclick", "findById("+post.id+')')
				));
			row.append($('<td>').append(
					$('<button>').text("削除").attr("type","button").attr("onclick", "deleteById("+post.id+')')
				));
			table.append(row);
		});

		$('#posts').append(table);
	}

}

function renderDetails(post) {
	$('.error').text('');
	$('#postId').val(post.id);
	$('#name').val(post.name);
}

function formToJSON() {
	var postId = $('#postId').val();
	return JSON.stringify({
		"id": (postId == "" ? 0 : postId),
		"name": $('#name').val()
	});
}
