import Ember from 'ember';

export default Ember.Controller.extend({
	loginFailed: false,
	requestFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	isRegistered: true,
	isAdmin: true,

	routes: Ember.computed('model.snapshots.[]', 'model.configuration', function() {
		var routes = [{
			route: 'profile',
			alias: '  Profile',
			icon: 'perm_identity'
		}, {
			route: 'analytics',
			alias: '  Analytics',
			icon: 'equalizer'
		}, {
			route: 'control',
			alias: '  Control',
			icon: 'settings_remote'
		}, {
			route: 'logout',
			alias: '  Sign Out',
			icon: 'lock_outline'
		}];
		if(this.get("isAdmin")) {
			routes = [{
				route: 'profile',
				alias: '  Profile',
				icon: 'perm_identity'
			}, {
				route: 'analytics',
				alias: '  Analytics',
				icon: 'equalizer'
			}, {
				route: 'control',
				alias: '  Control',
				icon: 'settings_remote'
			}, {
				route: 'newuser',
				alias: '  Add New User',
				icon: 'queue'
			}, {
				route: 'logout',
				alias: '  Sign Out',
				icon: 'lock_outline'
			}];
		}
		return routes;
	}),

	session: {
		isAuthenticated: false
	},

	actions: {
		transitionTo: function(e) {
			if(e.route == "logout") {
				window.location.reload(true);
			}
			if(e.route == "profile") {
				this.transitionToRoute('profile', {queryParams: {username: this.get("username")}});
			} else {
				this.transitionToRoute(e.route);
			}

		},

		login: function() {
			this.setProperties({
				loginFailed: false,
				isProcessing: true
			});

			this.set("timeout", setTimeout(this._actions.slowConnection.bind(this), 5000));

			var host = this.store.adapterFor('application').get('host'),
					namespace = this.store.adapterFor('application').namespace,
					postUrl = [ host, namespace, 'authenticate' ].join('/');
			var self = this;
			this.set('loading', true);
			$.ajax({
			    url: postUrl,
			    type: "POST",
			    data: this.getProperties("username", "password"),
					dataType: 'json',
					async: true,
			    success: function (response) {
							self._actions.success(self,response);
    			},
					error: function (jqXHR, textStatus, errorThrown) {
							self._actions.failure(self);
    			}
			});
		},

		success: function(self,response) {
			self._actions.reset(self);
			self.set("loginFailed", false);
			self.set("requestFailed", false);
			self.set('loading', false);
			self.set('session.isAuthenticated', true);
			self.set('isAdmin',response.data.attributes.isadmin)
			if (self.get('currentRouteName') == "index" || self.get('currentRouteName') == "register" ) {
					self.transitionToRoute("dashboard");
			}
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

		registered: function() {
			this.set("isRegistered", false);
		},

		gotoHome: function() {
			this.transitionToRoute("dashboard");
		}
	}
});
