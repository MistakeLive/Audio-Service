Vue.component('demo-grid', {
  template: '#grid-template',
  props: {
    data: Array,
    columns: Array,
    filterKey: String,
    count: Number,
    playlists: Array,
    cur: Number
  },
  data: function () {
    var sortOrders = {}
    this.columns.forEach(function (key) {
      sortOrders[key] = 1
    })
    return {
      sortKey: '',
      file: '',
      sortOrders: sortOrders
    }
  },
  computed: {
    filteredData: function () {
      var sortKey = this.sortKey
      var filterKey = this.filterKey && this.filterKey.toLowerCase()
      var order = this.sortOrders[sortKey] || 1
      var data = this.data
      if (filterKey) {
        data = data.filter(function (row) {
          return Object.keys(row).some(function (key) {
            return String(row[key]).toLowerCase().indexOf(filterKey) > -1
          })
        })
      }
      if (sortKey) {
        data = data.slice().sort(function (a, b) {
          a = a[sortKey]
          b = b[sortKey]
          return (a === b ? 0 : a > b ? 1 : -1) * order
        })
      }
      return data
    }
  },
  filters: {
    capitalize: function (str) {
      return str.charAt(0).toUpperCase() + str.slice(1)
    }
  },
  methods: {
    sortBy: function (key) {
      this.sortKey = key
      this.sortOrders[key] = this.sortOrders[key] * -1
    },

    submitFile(){

        formData = new FormData();

        formData.append('file', this.file);

        var ID;

        axios.post( '/add',
            formData,
            {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
          }
        ).then(function(response){
        ID = response.data
        axios.get('/info/' + response.data)
                .then(response =>{
                var song = {}
                song.name = response.data[0]
                song.author = response.data[1]
                song.duration = response.data[2]
                song.id = ID
                demo.addSong(song);
                this.count++
                })
        }
    )
    .catch(function(){
      console.log('FAILURE');
    });
  },

  handleFileUpload(){
    this.file = this.$refs.file.files[0];
  }
  }
})

var demo = new Vue({

  el: '#demo',
  data: {
    name: '',
    searchQuery: '',
    gridColumns: ['name', 'author', 'duration'],
    gridData: [],
    currentData: [],
    count: 0,
    id: 0,
    listName: '',
    playlistList: [],
    selected: -1,
    currentPlaylist: -1,
    inputName: ''

  },
  methods: {
  onSetSong: function (eventData) {
    this.id = eventData.id
    this.name = eventData.name
   },

   addSong: function(song) {
    this.gridData.push(song);
   },

   choosePlaylist: function(id){
    this.currentPlaylist = id
    this.currentData = []
    if (id == -1) {
        this.currentData = this.gridData
    } else {
        var pl;
        for (var j=0; j < this.playlistList.length; j++){
            if (this.playlistList[j].playlistId == id) {
             pl = this.playlistList[j];
             break;
            }
        }
        for (var j = 0; j < pl.songs.length; j++){
            for (var k = 0; k < this.gridData.length; k++){
                if (pl.songs[j] == this.gridData[k].id){
                    this.currentData.push(this.gridData[k])
                    }
            }
        }
    }
   },

   onAddSong: function(eventData){
    if(this.selected < 0) {
        alert("Create playlist and try later")
    }else{
        jQuery.ajax({
                url:     '/addSongToPlaylist', //url страницы
                type:     "POST", //метод отправки
                data: {
                    songId: eventData.id,
                    playlistId: this.selected
                 },
                success: function(response) { //Данные отправлены успешно
                    console.log('s')
                },
                error: function(response) { // Данные не отправлены
                    console.log('f')
                }
            });
        for (var i = 0; i < this.playlistList.length; i++){
            if (this.playlistList[i].playlistId == this.selected){

                if(this.playlistList[i].songs.indexOf(eventData.id) == -1)
                   this.playlistList[i].songs.push(eventData.id)
                   else alert("Song already added in this playlist")
                break;
            }
        }
    }
   },

   onDeleteSong: function(eventData){
    var tmp;
    for (var i = 0; i < this.playlistList.length; i++){
        if (this.playlistList[i].playlistId == this.currentPlaylist){
            tmp = this.playlistList[i]
            break;
            }
    }

    tmp.songs.splice(tmp.songs.indexOf(eventData.id), 1)

    for (var i = 0; i < this.currentData.length; i++){
            if (this.currentData[i].id == eventData.id)
                this.currentData.splice(i, 1)
        }

        jQuery.ajax({
                url:     '/deleteSongFromPlaylist', //url страницы
                type:     "POST", //метод отправки
                data: {
                    songId: eventData.id,
                    playlistId: this.selected
                 },
                success: function(response) { //Данные отправлены успешно
                    console.log('s')
                },
                error: function(response) { // Данные не отправлены
                    console.log('f')
                }
            });
   },

   addPlaylist: function(){
   jQuery.ajax({
       url:     '/addPlaylist', //url страницы
       type:     "POST", //метод отправки
       data: {
            name: this.inputName
       },
       success: function(data) { //Данные отправлены успешно
         var tmp = {}
         tmp.playlistId = data[0]
         tmp.songs = []
         tmp.userId = data[2]
         tmp.playlistName = data[3]
         demo.playlistList.push(tmp)
         demo.inputName = ""
        },
    error: function(response) { // Данные не отправлены
        console.log("f")
    }
    });
    }

   },
  created() {
    axios.get('/songsAndDur')
    .then(response => {
    while(response.data[this.count] != undefined){
        var song = {}
        song.name = response.data[this.count][0]
        song.author = response.data[this.count][1]
        song.duration = response.data[this.count][2]
        song.id = response.data[this.count][3]
        this.gridData.push(song)
        this.count++
    }
    this.currentData = this.gridData
     })

    axios.get('/userPlaylists')
    .then(response =>{
    var i = 0

    while(response.data[i] != undefined){
        var tmp = {}
        tmp.playlistId = response.data[i][0]
        tmp.songs = response.data[i][1]
        tmp.userId = response.data[i][2]
        tmp.playlistName = response.data[i][3]
        this.playlistList.push(tmp)
        i++
        }
        if (i != 0) {
            this.selected = this.playlistList[0].playlistId
        }

    })
  }

})
