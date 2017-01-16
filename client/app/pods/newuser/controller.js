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
	isAdmin: false,
	roomIds:null,

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	userHouseId: Ember.computed('model.user', function() {
		return this.get("model.user").get("house").slice()[0].get("id");
	}),

	userRooms: Ember.computed('model.user', function() {
		var self = this;
		var modelRooms = this.get("model.user").get("rooms");
		var rooms = [];
		modelRooms.forEach(function(room) {
			rooms.push({"name":room.get('name'),"id":room.get('id'),"hasAccess":false});
		})

		return rooms;
	}),

	rooms: [],

	roomsStringfy: Ember.computed('model.user', function() {
			return 	JSON.stringify(this.get("rooms"));
	}),

	actions: {
		submit: function() {
				var self = this;
				if ( this.get("name") == null || this.get("username") == null ||
				this.get("password") == null || this.get("cPassword") == null ||
				this.get("email") == null) {
					this.set("submitFailed", true);
				} else {
					if (this.get("password") == this.get("cPassword")) {
						this.setProperties({
							submitFailed: false,
							isProcessing: true
						});
						if(this.get("hasAccessToHome")) {
								this.get("userHouseId");
						} else {
							this.set("userHouseId","")
						}

						this.get("userRooms").forEach(function(room) {
							if(room.hasAccess) {
									self.get("rooms").push({"name":room.name,"id":room.id});
							}
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
										data: this.getProperties("name", "username","password","email","userHouseId","roomsStringfy","isAdmin"),
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
				self.transitionToRoute("dashboard");
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
		  }
		}
});
