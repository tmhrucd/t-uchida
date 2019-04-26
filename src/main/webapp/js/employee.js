'use strict';

var rootUrl = "/java_s04/api/v1.1/employees";
var getPostsUrl = "/java_s04/api/v1.1/posts";
var getPhotoUrl = "/java_s04/api/v1.1/photos";

initPage();

checkSession();

$('#saveEmployee').click(function() {
	$('.error').children().remove();
	if ($('#name').val() === '') {
		$('.error').append('<div>名前は必須入力です。</div>');
	}
	if ($('#empId').val() === '') {
		$('.error').append('<div>社員IDは必須入力です。</div>');
	}
	if ($('#empId').val() === '') {
		$('.error').append('<div>社員IDは必須入力です。</div>');
	}
	if ($('#age').val() === '') {
		$('.error').append('<div>年齢は必須入力です。</div>');
	}
	if ($('#gender').val() === '') {
		$('.error').append('<div>性別として男性、女性いずれかを選択してください。</div>');
	}
	if ($('#zip').val() === '') {
		$('.error').append('<div>郵便番号は必須入力です。</div>');
	}
	if ($('#pref').val() === '') {
		$('.error').append('<div>都道府県は必須入力です。</div>');
	}
	if ($('#address').val() === '') {
		$('.error').append('<div>住所は必須入力です。</div>');
	}
	if ($('#postId').val() === '') {
		$('.error').append('<div>いずれかの部署を選択してください。</div>');
	}
	if ($('.error').children().length != 0) {
		return false;
	}

	var id = $('#id').val()
	if (id === '')
		addEmployee();
	else
		updateEmployee(id);
	return false;
})

$('#findEmployee').click(function() {
	findByParam();
	return false;
})

$('#newEmployee').click(function() {
	renderDetails({});
});



function initPage() {
	var newOption = $('<option>').val(0).text('指定しない').prop('selected', true);
	$('#postIdParam').append(newOption);
	makePostSelection('#postIdParam');
	findAll();
	makePostSelection('#postId');
}

function findAll() {
	console.log('findAll start.')
	$.ajax({
		type : "GET",
		url : rootUrl,
		dataType : "json",
		success : renderTable
	});
}

function findById(id) {
	console.log('findByID start - id:' + id);
	$.ajax({
		type : "GET",
		url : rootUrl + '/' + id,
		dataType : "json",
		success : function(data) {
			console.log('findById success: ' + data.name);
			renderDetails(data)
		}
	});
}

function findByParam() {
	console.log('findByParam start.');

	var urlWithParam = rootUrl+'?postId='+$('#postIdParam').val()
		+'&empId='+$('#empIdParam').val()
		+'&nameParam='+$('#nameParam').val();
	$.ajax({
		type : "GET",
		url : urlWithParam,
		dataType : "json",
		success : renderTable
	});
}

function addEmployee() {
	console.log('addEmployee start');

	var fd = new FormData(document.getElementById("employeeForm"));

	$.ajax({
		url : rootUrl,
		type : "POST",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			alert('社員データの追加に成功しました');
			findAll();
			renderDetails(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('社員データの追加に失敗しました');
		}
	})
}

function updateEmployee(id) {
	console.log('updateEmployee start');

	var fd = new FormData(document.getElementById("employeeForm"));

	$.ajax({
		url : rootUrl + '/' + id,
		type : "PUT",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			alert('社員データの更新に成功しました');
			findAll();
			renderDetails(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('社員データの更新に失敗しました');
		}
	})
}

function deleteById(id) {
	console.log('delete start - id:' + id);
	$.ajax({
		type : "DELETE",
		url : rootUrl + '/' + id,
		success : function() {
			alert('社員データの削除に成功しました');
			findAll();
			renderDetails({});
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('社員データの削除に失敗しました');
		}
	});
}

function renderTable(data) {
	var headerRow = '<tr><th>社員ID</th><th>氏名</th></tr>';

	$('#employees').children().remove();

	if (data.length === 0) {
		$('#employees').append('<p>現在データが存在していません。</p>')
	} else {
		var table = $('<table>').attr('border', 1);
		table.append(headerRow);

		$.each(data, function(index, employee) {
			var row = $('<tr>');
			row.append($('<td>').text(employee.empId));
			row.append($('<td>').text(employee.name));
			row.append($('<td>').append(
					$('<button>').text("編集").attr("type","button").attr("onclick", "findById("+employee.id+')')
				));
			row.append($('<td>').append(
					$('<button>').text("削除").attr("type","button").attr("onclick", "deleteById("+employee.id+')')
				));
			table.append(row);
		});

		$('#employees').append(table);
	}
}

function renderDetails(employee) {
	$('.error').text('');
	$('#id').val(employee.id);
	$('#empId').val(employee.empId);
	$('#name').val(employee.name);
	$('#age').val(employee.age);
	$('input[name="gender"]').val([ employee.gender ]);

	$('#currentPhoto').children().remove();
	$('#photoId').val(employee.photoId);
	if (employee.photoId != null && employee.photoId != 0) {
		// 末尾のDate.now()はキャッシュ対策
		var currentPhoto = $('<img>').attr('src',
				getPhotoUrl + '/' + employee.photoId + '?' + Date.now());
		$('#currentPhoto').append(currentPhoto)
	}
	$('#zip').val(employee.zip);
	$('#pref').val(employee.pref);
	$('#address').val(employee.address);
	if (employee.post != null) {
		$('#postId').val(employee.post.id);
	}
	$('#enterDate').val(employee.enterDate);
	$('#retireDate').val(employee.retireDate);
}

function makePostSelection(selectionId, employee) {
	console.log('makePostSelection start.')
	$.ajax({
		type : "GET",
		url : getPostsUrl,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			$.each(data, function(index, post) {
				var newOption = $('<option>').val(post.id).text(post.name);
				if (employee != null && employee.post.id == post.id) {
					newOption.prop('selected', isSelected);
				}
				$(selectionId).append(newOption);
			});
		}
	});
}


$('#login-button').click(login);

/**ログインするファンクション**/
function login(){

	console.log('login start');

	var empId = $('#loginId').val();
	var logPass = $('#loginPass').val();

	var urlWithParam = rootUrl+'/login?empId='+empId+'&logPass='+logPass;

	console.log('社員ID='+empId+' パスワード='+logPass);

	$.ajax({
		url : urlWithParam,
		type : "POST",
		async : false,
		success : function(data) {

			alert(data);

			location.reload();

		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('通信に失敗しました。');
		}
	})

}
$('#logout').click(logout);

/**ログアウトするファンクション**/
function logout(){

	console.log('logout start');

	$.ajax({
		url : rootUrl + '/logout' ,
		type : "POST",
		async : false,
		success : function() {
			alert('ログアウトしました')

			location.reload();
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('通信に失敗しました。');
		}
	})

}



function checkSession(){

	console.log('checkSession start');

	$.ajax({
		url : rootUrl + '/session',
		type : "GET",
		async : false,
		success : function(data) {

			$('#login').append('<p>「'+data+'」でログインしています</p>');
			$('#login').append('<p><button id = "logout">ログアウト</button></p>');

		},
		error : function(jqXHR, textStatus, errorThrown) {

			$('#login').append('<form id="loginInf"><div><label for="logId">社員ID:</label><input type="text" maxlength="10" name="loginId" id="loginId"></div><div><label for="logPss">ログインパスワード:</label><input type="text" maxlength="10" name="loginPass" id="loginPass"></div></form><p><button id="login-button">ログイン</button></p>')

		}
	})
}

