// ステータスをレビューにしたら星とコメントのフィールドが表示される
const statusSelect = document.getElementById('statusSelect');
const reviewFields = document.getElementById('reviewFields');

statusSelect.addEventListener('change', function() {
    if (this.value === 'DONE') {
        reviewFields.style.display = 'block'; // レビューなら表示
    } else {
        reviewFields.style.display = 'none';  // 気になるリストなら非表示
    }
});

// タイプを選んだりタイトルを入力すると候補がサジェストされる
const titleInput = document.getElementById('titleInput');
const typeSelect = document.getElementById('typeSelect');
const suggestionList = document.getElementById('suggestionList');
let debounceTimer;

function executeSearch() {
    const query = titleInput.value;
    const type = typeSelect.value;

    if (query.trim().length < 2) {
        suggestionList.innerHTML = '';
        suggestionList.style.display = 'none';
        return;
    }

    // 全タイプ対応の新しいエンドポイント（想定）を叩く
    fetch(`/candidate?query=${encodeURIComponent(query)}&type=${type}`)
        .then(response => response.json())
        .then(data => displaySuggestions(data));
}

function displaySuggestions(data) {
    const suggestionList = document.getElementById('suggestionList');
    suggestionList.innerHTML = ''; // リストをリセット

    if (!data || data.length === 0) {
        suggestionList.style.display = 'none';
        return;
    }

    data.forEach(item => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'list-group-item list-group-item-action d-flex align-items-center gap-2';

        // 💡 MediaCandidate(title, imageUrl, releaseDate) のフィールド名に合わせる
        button.innerHTML = `
                <img src="${item.imageUrl || '/images/no-image.png'}" style="width: 40px; height: 55px; object-fit: cover;" alt="">
                <div class="text-start">
                    <div class="fw-bold" style="font-size: 0.9rem;">${item.title}</div>
                    <small class="text-muted">${item.releaseDate || ''}</small>
                </div>
            `;

        button.onclick = () => {
            document.getElementById('titleInput').value = item.title;
            suggestionList.style.display = 'none';
        };
        suggestionList.appendChild(button);
    });

    suggestionList.style.display = 'block';
}

titleInput.addEventListener('input', function() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(executeSearch, 300);
});

typeSelect.addEventListener('change', function() {
    titleInput.focus(); // タイトル欄にフォーカス
    executeSearch();    // すぐに再検索
});