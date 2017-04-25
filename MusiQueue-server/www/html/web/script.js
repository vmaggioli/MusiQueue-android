var hubInfo = {
    id: 0,
    name: '',
    isCreator: false,
    playingSong: false,
    songQueue: [],
    userLat: 0,
    userLong: 0,
};
var player; // the youtube player

function api(endpoint, params, callback) {
    if(callback == undefined) callback = function(){};
    _.defaults(params, {
        phoneId: localStorage.getItem("phoneId"),
        hubId: hubInfo.id,
    });
    $.ajax({
        type: "POST",
        url: "../api/" + endpoint,
        data: params,
        dataType: 'json',
        success: callback
    });
}

function init() {
    if(localStorage.getItem("phoneId") == null) {
        localStorage.setItem("phoneId", guid());
    }
    if(localStorage.getItem("username") == null) {
        $('#usernameScreen').removeClass('hidden');
    }else{
        $('#usernameScreen').addClass('hidden');
        $('#welcomeMessage').text("Welcome back, " + localStorage.getItem("username"));
    }
    hideLoadingScreen();

    // get location
    if("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(l => {
            hubInfo.userLat = l.coords.latitude;
            hubInfo.userLong = l.coords.longitude;
        });
    }
}
$(document).ready(init);


function hideLoadingScreen() {
    $('#loadingScreen').addClass('hidden');
}

function usernameSubmit() {
    var u = $('#usernameBox').val();
    u = u.trim();
    if(u == '') {
        alert("Please enter a username");
        return;
    }
    localStorage.setItem('username', u);
    $('#usernameScreen').addClass('hidden');
    $('#welcomeMessage').text("Hello, " + localStorage.getItem("username"));

}
$('#usernameButton').click(usernameSubmit);


function toCreateScreen() {
    $('#startScreen').addClass('hidden');
    $('#createScreen').removeClass('hidden');
}
$('#toCreateScreen').click(toCreateScreen);


function toHubSearchScreen() {
    $('#startScreen').addClass('hidden');
    $('#hubSearchScreen').removeClass('hidden');
}
$('#toHubSearchScreen').click(toHubSearchScreen);

function doHubSearch() {
    var n = $('#searchHubNameBox').val();
    api("searchHub.php", {hubName: n}, searchHubCallback);
}
$('#searchHubNameBox').keyup(doHubSearch);

function searchHubCallback(response) {
    $el = $("#searchHubResultArea");
    $el.empty();
    response.result.forEach(r => {
        $l = $('#hubTemplate').clone();
        $l.attr('id', '');
        $l.find('.name').text(r.hub_name);
        $l.find('.creator').text(r.hub_creator_name);
        if(r.hub_pin_required) {
            $l.addClass("locked");
        }
        if(r.is_rejoin) {
            $l.addClass('isRejoin');
        }
        $l.click({
            name: r.hub_name,
            pinRequired: r.hub_pin_required,
        }, hubListingClick);
        $el.append($l);
    });
}

function hubListingClick(e) {
    hubInfo.name = e.data.name;
    if(e.data.pinRequired) {
        toJoinScreen();
    } else {
        joinHubSubmit();
    }
}

function toJoinScreen() {
    $('#startScreen').addClass('hidden');
    $('#hubSearchScreen').addClass('hidden');
    $('#joinScreen').removeClass('hidden');
    $('#joinHubName').text(hubInfo.name);
}

function joinHubSubmit() {
    // hub name is already set in hubInfo
    api("joinHub.php", {
        hubName: hubInfo.name,
        hubPin: $('#joinHubPin').val(),
        username: localStorage['username'],
    }, joinCallback);
}
$('#joinHubSubmit').click(joinHubSubmit);

function joinCallback(response) {
    if(response.error) {
        if(response.errorCode == "HUB_PIN_WRONG") {
            alert("That hub pin is not correct!");
            $('#joinHubPin').val("");
            $('#joinHubPin').focus();
        }
        return;
    }
    $('#joinScreen').addClass('hidden');
    $('#hubSearchScreen').addClass('hidden');
    hubInfo.id = response.result.hub_id;
    hubInfo.isCreator = response.result.is_creator;
    initHub();
}


function createHubSubmit() {
    var n = $('#createHubName').val().trim();
    var p = $('#createHubPin').val().trim();
    if(n == "") {
        alert("You must choose a hub name");
        return;
    }
    if(! /[0-9]{4}/.test(p)) {
        alert("That pin is invalid");
        return;
    }
    api("createHub.php", {
        hubName: n,
        hubPin: p,
        lat: hubInfo.userLat,
        long: hubInfo.userLong,
        username: localStorage.getItem("username"),
    }, hubCreated);
    hubInfo.name = n;
    hubInfo.isCreator = true;
}
$('#createHubSubmit').click(createHubSubmit);

function hubCreated(response) {
    $('#createScreen').addClass('hidden');
    hubInfo.id = response.result.hub_id;
    initHub();
}


function initHub() {
    if(hubInfo.isCreator) {
        initPlayer();
    }

    loadSongQueue();
    setInterval(loadSongQueue, 4000);
}

function loadSongQueue() {
    api("hubSongList.php", {}, loadSongQueueCallback);
}

function loadSongQueueCallback(response) {
    hubInfo.songQueue = response.result.songs;
    showSongs();
    if(hubInfo.isCreator && !hubInfo.playingSong) {
        playNext();
    }
}

function showSongs() {
    var outer = $('#songList')[0];
    outer.innerHTML = "";
    hubInfo.songQueue.forEach(s => {
        $el = $('#songTemplate').clone();
        $el.attr('id', '');

        $el.find('.title').text(s.song_title);
        $el.find('.user').text(s.user_name);
        $el.find('.up').text(s.up_votes);
        $el.find('.down').text(s.down_votes);

        switch(s.voted) {
            case 1:
                $el.find('.up').addClass('pressed');
                break;
            case -1:
                $el.find('.down').addClass('pressed');
                break;
        }

        outer.appendChild($el[0]);
    });
}

function songDone() {
    if(hubInfo.songQueue.length != 0) {
        // remove the old song
        var video = hubInfo.songQueue.shift();
        api("removeSong.php", {songId: video.id});
        showSongs();
    }
    playNext();
}

function playNext() {
    if(hubInfo.songQueue.length == 0) {
        hubInfo.playingSong = false;
        return;
    }
    var video = hubInfo.songQueue[0];
    player.loadVideoById(video.song_id, 0, "small");
    hubInfo.playingSong = true;
}


/* PLAYER CODEE */
// https://developers.google.com/youtube/iframe_api_reference
function initPlayer() {
    var tag = document.createElement('script');
    tag.src = "http://www.youtube.com/iframe_api";
    document.body.appendChild(tag);
}

// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height: '240',
        width: '320',
        // playerVars: { controls: 0 },
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });
}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(event) {
    event.target.setVolume(100);
    // event.target.playVideo();
}

// 5. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
function onPlayerStateChange(event) {
    if(event.data == YT.PlayerState.ENDED)
        songDone();
}

function stopVideo() {
    player.stopVideo();
}
