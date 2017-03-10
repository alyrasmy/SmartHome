import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],
	submitFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	requestFailed: false,
	editMode:false,

	isAdmin: Ember.computed('model.user', function() {
		return this.get("model.user").get("isadmin");
	}),

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	name: Ember.computed('model.user', function() {
		return this.get("model.user").get("name");
	}),

	email: Ember.computed('model.user', function() {
		return this.get("model.user").get("email");
	}),

	username: Ember.computed('model.user', function() {
		return this.get("model.user").get("username");
	}),

	houseId: Ember.computed('model.user', function() {
		return this.get("model.user").get("house").slice()[0].get("id");
	}),

	rooms: Ember.computed('model.user', function() {
			var rooms=[];
			this.get("model.user").get("rooms").forEach( function(room) {
				rooms.push({"name":room.get("name"),"id":room.get("id")});
			});
			return rooms;
	}),

	roomsStringfy: Ember.computed('model.user', function() {
			return 	JSON.stringify(this.get("rooms"));
	}),

	tempthreshold: Ember.computed('model.user', function() {
			return this.get("model.user").get("tempthreshold");
	}),
	humidthreshold: Ember.computed('model.user', function() {
			return this.get("model.user").get("humidthreshold");
	}),
	ledthreshold: Ember.computed('model.user', function() {
			return this.get("model.user").get("ledthreshold");
	}),
	address: Ember.computed('model.user', function() {
			return this.get("model.user").get("address");
	}),
	city: Ember.computed('model.user', function() {
			return this.get("model.user").get("city");
	}),
	camera: Ember.computed('model.user', function() {
			return this.get("model.user").get("camera");
	}),

	actions: {
		submit: function() {
				var self = this;
				if ( this.get("name") == null || this.get("username") == null ||
				this.get("oldPassword") == null || this.get("password") == null ||
				this.get("email") == null  || this.get("houseId") == null ||
				this.get("tempthreshold") == null || this.get("humidthreshold") == null ||
				this.get("ledthreshold") == null || this.get("address") == null ||
				this.get("city") == null || this.get("camera") == null) {
					this.set("submitFailed", true);
				} else {
					if (this.get("oldPassword") == this.get("user").get("password")) {
						this.setProperties({
							submitFailed: false,
							isProcessing: true
						});
						var idexesToRemove = [];
						this.get("rooms").forEach( function(room,index) {
							if(!room.name || !room.id) {
									idexesToRemove.push(index);
							}
						});
						idexesToRemove.forEach( function(index) {
							self.get("rooms").splice(index, 1);
						});

						this.set("timeout", setTimeout(this._actions.slowConnection.bind(this), 5000));
						var host = this.store.adapterFor('application').get('host'),
								namespace = this.store.adapterFor('application').namespace,
								postUrl = [ host, namespace, 'user/create' ].join('/');
								this.set('loading', true);
								var self = this;
								$.ajax({
										url: postUrl,
										type: "POST",
										data: this.getProperties("name", "username","password",
										"email","houseId","roomsStringfy","isAdmin","tempthreshold",
										"humidthreshold","ledthreshold","address","city",
										"camera"),
										dataType: 'text',
										async: true,
										success: function (response) {
												self._actions.success(self,response);
										},
										error: function (jqXHR, textStatus, errorThrown) {
												self._actions.failure(self);
										}
								});
					} else { // both password fields are not equal. Throw invalid input warning
						this.set("submitFailed", true);
					}
				}
		  },

			success: function(self,response) {
				self._actions.reset(self);
				self.set("requestFailed", false);
				self.set("submitFailed", false);
				self.set('loading', false);
				self.set("editMode",false);
			},

		  failure: function(self) {
		    self._actions.reset(self);
				self.set('loading', false);
		    self.set("requestFailed", true);
		  },

		  slowConnection: function() {
		    this.set("isSlowConnection", true);
		  },

		  reset: function(self) {
		    clearTimeout(self.get("timeout"));
		    self.setProperties({
		      isProcessing: false,
		      isSlowConnection: false
		    });
		  },

			cancel: function() {
				this.set("editMode",false);
				this.set("requestFailed", false);
			},

			editable: function() {
				this.set("editMode",true);
				this.get("user");
			},

			addRoom: function() {
				this.get("rooms").pushObject({"name":"","id":""});
			},

			removeRoom: function(id) {
				var self = this;
				var idexesToRemove = [];
				this.get("rooms").forEach( function(room,index) {
					if(room.id == id) {
							idexesToRemove.push(index);
					}
				});
				idexesToRemove.forEach( function(index) {
					self.get("rooms").splice(index, 1);
				});
				this.set("rooms",this.get("rooms").slice())
			},
		}
});
