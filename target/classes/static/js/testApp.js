Vue.component('demo-grid', {
  template: '#grid-template',
  props: {
    data: Array,
    columns: Array,
    filterKey: String,
    count: Number
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

                axios.post( '/',
                    formData,
                    {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                  }
                ).then(function(response){
                console.log('/info/' + response.data)
                ID = response.data
                axios.get('/info/' + response.data)
                        .then(response =>{
                        var song = {}
                        console.log(response.data)
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
    count: 0,
    id: 0

  },
  methods: {
  onSetSong: function (eventData) {
    this.id = eventData.id
    this.name = eventData.name
   },
   addSong: function(song) {
    this.gridData.push(song);
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
     })
  }
})