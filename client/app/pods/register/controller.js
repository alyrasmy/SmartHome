import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],
	loginFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	isAdmin:true, //if your registering you are therefore an admin

	actions: {
		  register: function() {
				if ( this.get("name") == null || this.get("username") == null ||
				this.get("password") == null || this.get("cPassword") == null ||
				this.get("email") == null  || this.get("houseId") == null ||
				this.get("roomIds") == null) {
					this.set("loginFailed", true);
				} else {
					if (this.get("password") == this.get("cPassword")) {
						this.setProperties({
							loginFailed: false,
							isProcessing: true
						});
						this.set("timeout", setTimeout(this._actions.slowConnection.bind(this), 5000));
						var host = this.store.adapterFor('application').get('host'),
								namespace = this.store.adapterFor('application').namespace,
								postUrl = [ host, namespace, 'user/create' ].join('/');
						var request = $.post(postUrl, this.getProperties("name", "username","password","email","houseId","roomIds","isAdmin"));
						this.set('loading', true);
						request.then(this._actions.success.bind(this), this._actions.failure.bind(this));
					} else { // both password fields are not equal. Throw invalid input warning
						this.set("loginFailed", true);
					}
				}
		  },

		  success: function() {
				var self = this;
		    this_actions.reset(self);
				this.set("loginFailed", false);
				this.set('loading', false);
				this.set("controllers.application.isRegistered", true);
				this.transitionToRoute("dashboard");
		  },

		  failure: function() {
				var self = this;
		    this._actions.reset(self);
				this.set('loading', false);
		    this.set("loginFailed", true);
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
				this.transitionToRoute("dashboard");
				this.set("controllers.application.isRegistered", true);
			}
		}
});
