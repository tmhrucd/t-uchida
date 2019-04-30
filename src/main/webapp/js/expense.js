'use strict';

var rootUrl = "/java_s04/api/v1.1/employees";
var getPostsUrl = "/java_s04/api/v1.1/posts";
var getPhotoUrl = "/java_s04/api/v1.1/photos";
var expenseUrl = "/java_s04/api/v1.1/expenses";
var getStatusesUrl = "/java_s04/api/v1.1/statuses";


/**着手済・着手中**/

initPage();


/**セッション確認⇒表示**/
checkSession();

/**ログイン**/
$('#login-button').click(login);


function initPage() {
	var newOption = $('<option>').val(0).text('指定しない').prop('selected', true);
	$('#statusIdParam').append(newOption);
	makeStatusSelection('#statusIdParam');

	//ログインしているか否か
	$.ajax({
		url : rootUrl + '/EmpId',
		type : "GET",
		async : false,
		success : function(data) {

			if(data == 'false')
			{
				$('#expenses').children().remove();
				$('#expenses').append('<p>ログインしてください。</p>')

			}
			else{

				findAll();

			}

		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	})


//	makeStatusSelection('#status');
}

/**全件表示**/
function findAll() {
	console.log('findAll start.')
	$.ajax({
		type : "GET",
		url : expenseUrl,
		dataType : "json",
		success : renderTable
	});
}


function renderTable(data) {
	var headerRow = '<tr><th>申請ID</th><th>申請日</th><th>更新日</th><th>申請者</th><th>タイトル</th><th>金額</th><th>ステータス</th><</tr>';

	$('#expenses').children().remove();



	if (data.length === 0) {
		$('#expenses').append('<p>現在データが存在していません。</p>')
	} else {
		var table = $('<table>').attr('border', 1);
		table.append(headerRow);

		$.each(data, function(index, expense) {
			var row = $('<tr>');
			row.append($('<td>').text(expense.id));
			row.append($('<td>').text(expense.reportDate));
			row.append($('<td>').text(expense.updateDate));
			row.append($('<td>').text(expense.name));
			row.append($('<td>').text(expense.title));
			row.append($('<td>').text(changeYen(expense.money)));
			row.append($('<td>').text(expense.status));
			row.append($('<td>').append(
					$('<button>').text("詳細").attr("type","button").attr("onclick", "findById("+expense.id+')')
				));
			table.append(row);
		});

		$('#expenses').append(table);
	}
}

/**金額表示のファンクション**/
function changeYen(num){
    return '¥' + String(num).split("").reverse().join("").match(/\d{1,3}/g).join(",").split("").reverse().join("");
}



function makeStatusSelection(selectionId, expense) {
	console.log('makeStatusSelection start.')
	$.ajax({
		type : "GET",
		url : getStatusesUrl,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			$.each(data, function(index, status) {
				var newOption = $('<option>').val(status.id).text(status.status);
				if (expense != null && expense.post.id == status.id) {
					newOption.prop('selected', isSelected);
				}
				$(selectionId).append(newOption);
			});
		}
	});
}




/**検索ファンクション**/
function findByParam() {
	console.log('findByParam start.');

	var urlWithParam = expenseUrl+'?statusId='+$('#statusIdParam').val()
		+'&nameParam='+$('#nameParam').val();
	$.ajax({
		type : "GET",
		url : urlWithParam,
		dataType : "json",
		success : renderTable
	});
}


/**検索ボタン押したとき**/
$('#findExpense').click(function() {

	//ログインしているか否か
	$.ajax({
		url : rootUrl + '/EmpId',
		type : "GET",
		async : false,
		success : function(data) {

			if(data == 'false')
			{
				$('#expenses').children().remove();
				$('#expenses').append('<p>ログインしてください。</p>')
			}
			else{

				findByParam();

			}

		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	})

	return false;
})


function checkAddExpense(){

	/**未ログイン**/
	if($('#reportEmpId').val() == 'ログインしてください'){

		alert('経費申請にはログインが必要です。')

	}
	/**新規申請ボタン押してない**/
	else if ($('#status').val() === '') {

		alert('新規申請ボタンを押してください。')

	}
	/**ログイン中**/
	else{

		$('.error').children().remove();
		if ($('#reportDate').val() === '') {
			$('.error').append('<div>申請日は必須入力です。</div>');
		}
		if ($('#reportEmpId').val() === '') {
			$('.error').append('<div>申請者社員IDは必須入力です。</div>');
		}
		if ($('#reportName').val() === '') {
			$('.error').append('<div>申請者名は必須入力です。</div>');
		}
		if ($('#title').val() === '') {
			$('.error').append('<div>タイトルは必須入力です。</div>');
		}
		if ($('#place').val() === '') {
			$('.error').append('<div>支払先は必須入力です。</div>');
		}
		if ($('#money').val() === '') {
			$('.error').append('<div>金額は必須入力です。</div>');
		}
		if ($('.error').children().length != 0) {
			return false;
		}

		var id = $('#id').val()
		if (id === '')
			addExpense();

//		else
//		updateEmployee(id);


	}

	return false;
}


/**新規追加ボタンで空に**/
$('#newExpense').click(function() {
	renderDetails({});
	/**入力権限**/
	detailAbled();
	detailAddExpense()


	//項目自動入力
	$('#status').val('申請中');

	$.ajax({
		url : rootUrl + '/EmpId',
		type : "GET",
		async : false,
		success : function(empId) {

			if(empId == 'false'){

				$('#reportEmpId').val('ログインしてください')

			}
			else{

				/**社員ID表示**/
				$('#reportEmpId').val(empId);

				/**社員IDから社員名取得**/
				$.ajax({
					url : rootUrl + '/emp/'+empId,
					type : "GET",
					async : false,
					success : function(employee) {

						/**申請者名表示**/
						$('#reportName').val(employee.name);

					},
					error : function(jqXHR, textStatus, errorThrown) {

						alert('データの通信に失敗しました。')

					}
				});

		     }

		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	})

	deleteButton();
	$('#addExpense-button').html('<button id="addExpense">申請</button>').attr("onclick", "checkAddExpense()");

});


/**社員IDから社員情報取得**/
function getEmpData(empId){

	$.ajax({
		url : rootUrl + '/emp/'+empId,
		type : "GET",
		async : false,
		success : function(data) {


		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	});


}


/**詳細表示**/
function renderDetails(expense) {
	$('.error').text('');
	$('#id').val(expense.id);
	$('#reportDate').val(expense.reportDate);
	$('#updateDate').val(expense.updateDate);
	$('#reportEmpId').val(expense.empId);
	$('#reportName').val(expense.name);
	$('#title').val(expense.title);
	$('#place').val(expense.place);
	$('#money').val(expense.money);
	$('#status').val(expense.status);
	$('#updateEmpId').val(expense.updateEmpId);
	$('#updateName').val(expense.updateName);
	$('#reason').val(expense.reason);
}



/**新規申請追加**/
function addExpense() {
	console.log('addExpense start');

	var fd = new FormData(document.getElementById("expenseForm"));

	$.ajax({
		url : expenseUrl,
		type : "POST",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			alert('申請に成功しました');
			findAll();
			renderDetails({});
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('申請に失敗しました');
		}
	})
}



/**ログインするファンクション**/
function login(){

	console.log('login start');

	var fd = new FormData(document.getElementById("loginInf"));


	$.ajax({
		url : rootUrl+'/login',
		type : "POST",
		data : fd,
		contentType : false,
		processData : false,
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


/**セッションチェックし、表示非表示**/
function checkSession(){

	console.log('checkSession start');

	$.ajax({
		url : rootUrl + '/session',
		type : "GET",
		async : false,
		success : function(data) {

			if(data != 'false'){

				$('#login').append('<p>「'+data+'」でログインしています</p>');
				$('#login').append('<p><button id = "logout">ログアウト</button></p>');

			}
			else{

				$('#login').append('<form id="loginInf"><div><label for="logId">社員ID:</label><input type="text" maxlength="10" name="loginId" id="loginId"></div><div><label for="logPss">ログインパスワード:</label><input type="text" maxlength="10" name="loginPass" id="loginPass"></div></form><p><button id="login-button">ログイン</button></p>')

			}



		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	})
}


/**セッションを確認し、社員IDを取得**/
function getEmpIdBySession(){

	console.log('checkEmpID start');

	$.ajax({
		url : rootUrl + '/EmpId',
		type : "GET",
		async : false,
		success : function(data) {

			alert(data);
			return data;

		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	})

}



/**セッションを確認し、権限IDを取得**/
function getAuthIdBySession(){

	console.log('checkAuthID start');

	$.ajax({
		url : rootUrl + '/AuthId',
		type : "GET",
		async : false,
		success : function(data) {

			alert(data);
			return data;

		},
		error : function(jqXHR, textStatus, errorThrown) {

			alert('データの通信に失敗しました。')

		}
	})

}


$('#SeEmpId').click(getEmpIdBySession);
$('#SeAuthId').click(getAuthIdBySession);


/**詳細ボタンで呼び出される**/
function findById(id) {
	console.log('findByID start - id:' + id);
	$.ajax({
		type : "GET",
		url : expenseUrl + '/' + id,
		dataType : "json",
		async : false,
		success : function(data) {
			console.log('findById success: ' + data.name);
			renderDetails(data);

			/**詳細時の権限**/
			detailApprove();

			/**一旦ボタン削除**/
			deleteButton();

			/**申請中**/
			if(data.statusId === 1){


				/**セッションから権限ID確認**/
				$.ajax({
					url : rootUrl + '/AuthId',
					type : "GET",
					async : false,
					success : function(auth) {

						/**承認者権限**/
						if(auth === '1'){

							/**承認ボタンにidを**/
							$('#approve-button').html('<button id="approve">承認</button>');
							$('#approve').attr("onclick", "approveExpense("+data.id+')');

							/**却下ボタンにidを**/
							$('#reject-button').html('<button id="reject">却下</button>');
							$('#reject').attr("onclick", "rejectExpense("+data.id+')')

						}
						/**依頼者権限**/
						else{
							/**詳細をdisabled**/
							detailDisabled();
						}

					},
					error : function(jqXHR, textStatus, errorThrown) {

						alert('データの通信に失敗しました。')

					}
				})

			}
			/**承認済or却下**/
			else if(data.statusId === 2 || data.statusId === 3){

				/**詳細をdisabled**/
				detailDisabled();

			}
			else if(data.statusId === 2){

				$('#reason-div').html('');

			}



		}
	});
}



/**承認ファンクション**/
function approveExpense(id) {
	console.log('updateEmployee start');

	$('.error').children().remove();

	if ($('#updateDate').val() === '') {
		$('.error').append('<div>更新日は必須入力です。</div>');
	}

	if ($('.error').children().length != 0) {
		return false;
	}


	var fd = new FormData(document.getElementById("expenseForm"));

	$.ajax({
		url : expenseUrl + '/approve/' + id,
		type : "PUT",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		async : false ,
		success : function(data, textStatus, jqXHR) {
			alert('申請を承認しました');
			findAll();
			renderDetails(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('承認に失敗しました');
		}
	})
}

/**却下ファンクション**/
function rejectExpense(id) {
	console.log('rejectEmployee start');

	$('.error').children().remove();

	if ($('#updateDate').val() === '') {
		$('.error').append('<div>更新日は必須入力です。</div>');
	}
	if ($('#reason').val() === '') {
		$('.error').append('<div>却下理由は必須入力です。</div>');
	}
	if ($('.error').children().length != 0) {
		return false;
	}

	var fd = new FormData(document.getElementById("expenseForm"));

	$.ajax({
		url : expenseUrl + '/reject/' + id,
		type : "PUT",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		async : false ,
		success : function(data, textStatus, jqXHR) {
			alert('申請を却下しました');
			findAll();
			renderDetails(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('却下に失敗しました');
		}
	})
}

/**各種ボタンを消すファンクション**/
function deleteButton(){

	/**承認ボタンを消す**/
	$('#approve-button').html('')
	/**却下ボタンにidを**/
	$('#reject-button').html('');
	/**申請ボタンを消す**/
	$('#addExpense-button').html('');


}

/**詳細をすべてdisabledに**/
function detailDisabled(){

	/**操作不能項目はreadonlyに**/
	$('#id').prop('disabled', true);
	$('#reportDate').prop('disabled', true);
	$('#updateDate').prop('disabled', true);
	$('#reportEmpId').prop('disabled', true);
	$('#reportName').prop('disabled', true);
	$('#title').prop('disabled', true);
	$('#place').prop('disabled', true);
	$('#money').prop('disabled', true);
	$('#updateEmpId').prop('disabled', true);
	$('#updateName').prop('disabled', true);
	$('#reason').prop('disabled', true);


}

/**詳細をすべてabledに**/
function detailAbled(){

	/**操作不能項目はreadonlyに**/
	$('#id').prop('disabled', false).prop('readonly', false);
	$('#reportDate').prop('disabled', false).prop('readonly', false);
	$('#updateDate').prop('disabled', false).prop('readonly', false);
	$('#reportEmpId').prop('disabled', false).prop('readonly', false);
	$('#reportName').prop('disabled', false).prop('readonly', false);
	$('#title').prop('disabled', false).prop('readonly', false);
	$('#place').prop('disabled', false).prop('readonly', false);
	$('#money').prop('disabled', false).prop('readonly', false);
	$('#updateEmpId').prop('disabled', false).prop('readonly', false);
	$('#updateName').prop('disabled', false).prop('readonly', false);
	$('#reason').prop('disabled', false).prop('readonly', false);


}


/**承認時**/
function detailApprove(){

	/**送信不要項目はreadonlyに**/
	$('#id').prop('disabled', true);
	$('#reportDate').prop('readonly', true);
	$('#updateDate').prop('readonly', false);
	$('#reportEmpId').prop('readonly', true);
	$('#reportName').prop('readonly', true);
	$('#title').prop('readonly', true);
	$('#place').prop('readonly', true);
	$('#money').prop('readonly', true);
	$('#status').prop('readonly', true);
	$('#updateEmpId').prop('readonly', true);
	$('#updateName').prop('disable', true);
	$('#reason').prop('readonly',false);


}

/**新規追加時**/
function detailAddExpense(){

	/**送信不要項目はreadonlyに**/
	$('#id').prop('disabled', true);
	$('#reportDate').prop('readonly', false);
	$('#updateDate').prop('readonly', true);
	$('#reportEmpId').prop('readonly', true);
	$('#reportName').prop('readonly', true);
	$('#title').prop('readonly', false);
	$('#place').prop('readonly', false);
	$('#money').prop('readonly', false);
	$('#status').prop('disabled', true);
	$('#updateEmpId').prop('readonly', true);
	$('#updateName').prop('disabled', true);
	$('#reason').prop('readonly', true);



}




