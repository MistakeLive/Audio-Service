Vue.component('demo-grid', {
  template: '#grid-template',
  props: {
    data: Array,
    columns: Array,
    filterKey: String
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

                axios.post( '/',
                    formData,
                    {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                  }
                ).then(function(){
                console.log('SUCCESS');
            })
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
    massage: '',
    searchQuery: '',
    gridColumns: ['name', 'duration'],
    gridData: []

  },
  methods: {
  onSetSong: function (nameSong) {

    this.massage = nameSong
   }

   },
  created() {
    axios.get('/songsAndDur')
    .then(response => {
    i = 0
    while(response.data[i] != undefined){
        var song = {}
        song.name = response.data[i][0]
        song.duration = response.data[i][1]
        this.gridData.push(song)
        i++
    }
        })
  }
})