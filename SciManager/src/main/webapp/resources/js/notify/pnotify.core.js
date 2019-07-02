! function(t) {
    "function" == typeof define && define.amd ? define("pnotify", ["jquery"], t) : t(jQuery)
}(function(t) {
    var i, s, o = {
            dir1: "down",
            dir2: "left",
            push: "bottom",
            spacing1: 25,
            spacing2: 25,
            context: t("body")
        },
        e = t(window),
        n = function() {
            s = t("body"), PNotify.prototype.options.stack.context = s, e = t(window), e.bind("resize", function() {
                i && clearTimeout(i), i = setTimeout(function() {
                    PNotify.positionAll(!0)
                }, 10)
            })
        };
    return PNotify = function(t) {
        this.parseOptions(t), this.init()
    }, t.extend(PNotify.prototype, {
        version: "2.0.1",
        options: {
            title: !1,
            title_escape: !1,
            text: !1,
            text_escape: !1,
            styling: "bootstrap3",
            addclass: "",
            cornerclass: "",
            auto_display: !0,
            width: "300px",
            min_height: "16px",
            type: "notice",
            icon: !0,
            opacity: 1,
            animation: "fade",
            animate_speed: "slow",
            position_animate_speed: 500,
            shadow: !0,
            hide: !0,
            delay: 8e3,
            mouse_reset: !0,
            remove: !0,
            insert_brs: !0,
            destroy: !0,
            stack: o
        },
        modules: {},
        runModules: function(t, i) {
            var s;
            for (var o in this.modules) s = "object" == typeof i && o in i ? i[o] : i, "function" == typeof this.modules[o][t] && this.modules[o][t](this, "object" == typeof this.options[o] ? this.options[o] : {}, s)
        },
        state: "initializing",
        timer: null,
        styles: null,
        elem: null,
        container: null,
        title_container: null,
        text_container: null,
        animating: !1,
        timerHide: !1,
        init: function() {
            var i = this;
            return this.modules = {}, t.extend(!0, this.modules, PNotify.prototype.modules), this.styles = "object" == typeof this.options.styling ? this.options.styling : PNotify.styling[this.options.styling], this.elem = t("<div />", {
                "class": "ui-pnotify " + this.options.addclass,
                css: {
                    display: "none"
                },
                mouseenter: function() {
                    if (i.options.mouse_reset && "out" === i.animating) {
                        if (!i.timerHide) return;
                        i.cancelRemove()
                    }
                    i.options.hide && i.options.mouse_reset && i.cancelRemove()
                },
                mouseleave: function() {
                	i.options.hide && i.options.mouse_reset && i.queueRemove(), PNotify.positionAll()
                }
            }), this.container = t("<div />", {
                "class": this.styles.container + " ui-pnotify-container " + ("error" === this.options.type ? this.styles.error : "info" === this.options.type ? this.styles.info : "success" === this.options.type ? this.styles.success : "dark" === this.options.type ? this.styles.dark : this.styles.notice)
            }).appendTo(this.elem), "" !== this.options.cornerclass && this.container.removeClass("ui-corner-all").addClass(this.options.cornerclass), this.options.shadow && this.container.addClass("ui-pnotify-shadow"), this.options.icon !== !1 && t("<div />", {
                "class": "ui-pnotify-icon"
            }).append(t("<span />", {
                "class": this.options.icon === !0 ? "error" === this.options.type ? this.styles.error_icon : "info" === this.options.type ? this.styles.info_icon : "success" === this.options.type ? this.styles.success_icon : "dark" === this.options.type ? this.styles.dark_icon : this.styles.notice_icon : this.options.icon
            })).prependTo(this.container), this.title_container = t("<h4 />", {
                "class": "ui-pnotify-title"
            }).appendTo(this.container), this.options.title === !1 ? this.title_container.hide() : this.options.title_escape ? this.title_container.text(this.options.title) : this.title_container.html(this.options.title), this.text_container = t("<div />", {
                "class": "ui-pnotify-text"
            }).appendTo(this.container), this.options.text === !1 ? this.text_container.hide() : this.options.text_escape ? this.text_container.text(this.options.text) : this.text_container.html(this.options.insert_brs ? String(this.options.text).replace(/\n/g, "<br />") : this.options.text), "string" == typeof this.options.width && this.elem.css("width", this.options.width), "string" == typeof this.options.min_height && this.container.css("min-height", this.options.min_height), PNotify.notices = "top" === this.options.stack.push ? t.merge([this], PNotify.notices) : t.merge(PNotify.notices, [this]), "top" === this.options.stack.push && this.queuePosition(!1, 1), this.options.stack.animation = !1, this.runModules("init"), this.options.auto_display && this.open(), this
        },
        update: function(i) {
            var s = this.options;
            return this.parseOptions(s, i), this.options.cornerclass !== s.cornerclass && this.container.removeClass("ui-corner-all " + s.cornerclass).addClass(this.options.cornerclass), this.options.shadow !== s.shadow && (this.options.shadow ? this.container.addClass("ui-pnotify-shadow") : this.container.removeClass("ui-pnotify-shadow")), this.options.addclass === !1 ? this.elem.removeClass(s.addclass) : this.options.addclass !== s.addclass && this.elem.removeClass(s.addclass).addClass(this.options.addclass), this.options.title === !1 ? this.title_container.slideUp("fast") : this.options.title !== s.title && (this.options.title_escape ? this.title_container.text(this.options.title) : this.title_container.html(this.options.title), s.title === !1 && this.title_container.slideDown(200)), this.options.text === !1 ? this.text_container.slideUp("fast") : this.options.text !== s.text && (this.options.text_escape ? this.text_container.text(this.options.text) : this.text_container.html(this.options.insert_brs ? String(this.options.text).replace(/\n/g, "<br />") : this.options.text), s.text === !1 && this.text_container.slideDown(200)), this.options.type !== s.type && this.container.removeClass(this.styles.error + " " + this.styles.notice + " " + this.styles.success + " " + this.styles.info).addClass("error" === this.options.type ? this.styles.error : "info" === this.options.type ? this.styles.info : "success" === this.options.type ? this.styles.success : this.styles.notice), (this.options.icon !== s.icon || this.options.icon === !0 && this.options.type !== s.type) && (this.container.find("div.ui-pnotify-icon").remove(), this.options.icon !== !1 && t("<div />", {
                "class": "ui-pnotify-icon"
            }).append(t("<span />", {
                "class": this.options.icon === !0 ? "error" === this.options.type ? this.styles.error_icon : "info" === this.options.type ? this.styles.info_icon : "success" === this.options.type ? this.styles.success_icon : this.styles.notice_icon : this.options.icon
            })).prependTo(this.container)), this.options.width !== s.width && this.elem.animate({
                width: this.options.width
            }), this.options.min_height !== s.min_height && this.container.animate({
                minHeight: this.options.min_height
            }), this.options.opacity !== s.opacity && this.elem.fadeTo(this.options.animate_speed, this.options.opacity), this.options.hide ? s.hide || this.queueRemove() : this.cancelRemove(), this.queuePosition(!0), this.runModules("update", s), this
        },
        open: function() {
            this.state = "opening", this.runModules("beforeOpen");
            var t = this;
            return this.elem.parent().length || this.elem.appendTo(this.options.stack.context ? this.options.stack.context : s), "top" !== this.options.stack.push && this.position(!0), "fade" === this.options.animation || "fade" === this.options.animation.effect_in ? this.elem.show().fadeTo(0, 0).hide() : 1 !== this.options.opacity && this.elem.show().fadeTo(0, this.options.opacity).hide(), this.animateIn(function() {
                t.queuePosition(!0), t.options.hide && t.queueRemove(), t.state = "open", t.runModules("afterOpen")
            }), this
        },
        remove: function(i) {
            this.state = "closing", this.timerHide = !!i, this.runModules("beforeClose");
            var s = this;
            return this.timer && (window.clearTimeout(this.timer), this.timer = null), this.animateOut(function() {
                if (s.state = "closed", s.runModules("afterClose"), s.queuePosition(!0), s.options.remove && s.elem.detach(), s.runModules("beforeDestroy"), s.options.destroy && null !== PNotify.notices) {
                    var i = t.inArray(s, PNotify.notices); - 1 !== i && PNotify.notices.splice(i, 1)
                }
                s.runModules("afterDestroy")
            }), this
        },
        get: function() {
            return this.elem
        },
        parseOptions: function(i, s) {
            this.options = t.extend(!0, {}, PNotify.prototype.options), this.options.stack = PNotify.prototype.options.stack;
            var o, e = [i, s];
            for (var n in e) {
                if (o = e[n], "undefined" == typeof o) break;
                if ("object" != typeof o) this.options.text = o;
                else
                    for (var a in o) this.modules[a] ? t.extend(!0, this.options[a], o[a]) : this.options[a] = o[a]
            }
        },
        animateIn: function(t) {
            this.animating = "in";
            var i;
            i = "undefined" != typeof this.options.animation.effect_in ? this.options.animation.effect_in : this.options.animation, "none" === i ? (this.elem.show(), t()) : "show" === i ? this.elem.show(this.options.animate_speed, t) : "fade" === i ? this.elem.show().fadeTo(this.options.animate_speed, this.options.opacity, t) : "slide" === i ? this.elem.slideDown(this.options.animate_speed, t) : "function" == typeof i ? i("in", t, this.elem) : this.elem.show(i, "object" == typeof this.options.animation.options_in ? this.options.animation.options_in : {}, this.options.animate_speed, t), this.elem.parent().hasClass("ui-effects-wrapper") && this.elem.parent().css({
                position: "fixed",
                overflow: "visible"
            }), "slide" !== i && this.elem.css("overflow", "visible"), this.container.css("overflow", "hidden")
        },
        animateOut: function(t) {
            this.animating = "out";
            var i;
            i = "undefined" != typeof this.options.animation.effect_out ? this.options.animation.effect_out : this.options.animation, "none" === i ? (this.elem.hide(), t()) : "show" === i ? this.elem.hide(this.options.animate_speed, t) : "fade" === i ? this.elem.fadeOut(this.options.animate_speed, t) : "slide" === i ? this.elem.slideUp(this.options.animate_speed, t) : "function" == typeof i ? i("out", t, this.elem) : this.elem.hide(i, "object" == typeof this.options.animation.options_out ? this.options.animation.options_out : {}, this.options.animate_speed, t), this.elem.parent().hasClass("ui-effects-wrapper") && this.elem.parent().css({
                position: "fixed",
                overflow: "visible"
            }), "slide" !== i && this.elem.css("overflow", "visible"), this.container.css("overflow", "hidden")
        },
        position: function(t) {
            var i = this.options.stack,
                o = this.elem;
            if (o.parent().hasClass("ui-effects-wrapper") && (o = this.elem.css({
                    left: "0",
                    top: "0",
                    right: "0",
                    bottom: "0"
                }).parent()), "undefined" == typeof i.context && (i.context = s), i) {
                "number" != typeof i.nextpos1 && (i.nextpos1 = i.firstpos1), "number" != typeof i.nextpos2 && (i.nextpos2 = i.firstpos2), "number" != typeof i.addpos2 && (i.addpos2 = 0);
                var n = "none" === o.css("display");
                if (!n || t) {
                    var a, p, h, c = {};
                    switch (i.dir1) {
                        case "down":
                            h = "top";
                            break;
                        case "up":
                            h = "bottom";
                            break;
                        case "left":
                            h = "right";
                            break;
                        case "right":
                            h = "left"
                    }
                    a = parseInt(o.css(h).replace(/(?:\..*|[^0-9.])/g, "")), isNaN(a) && (a = 0), "undefined" != typeof i.firstpos1 || n || (i.firstpos1 = a, i.nextpos1 = i.firstpos1);
                    var r;
                    switch (i.dir2) {
                        case "down":
                            r = "top";
                            break;
                        case "up":
                            r = "bottom";
                            break;
                        case "left":
                            r = "right";
                            break;
                        case "right":
                            r = "left"
                    }
                    if (p = parseInt(o.css(r).replace(/(?:\..*|[^0-9.])/g, "")), isNaN(p) && (p = 0), "undefined" != typeof i.firstpos2 || n || (i.firstpos2 = p, i.nextpos2 = i.firstpos2), ("down" === i.dir1 && i.nextpos1 + o.height() > (i.context.is(s) ? e.height() : i.context.prop("scrollHeight")) || "up" === i.dir1 && i.nextpos1 + o.height() > (i.context.is(s) ? e.height() : i.context.prop("scrollHeight")) || "left" === i.dir1 && i.nextpos1 + o.width() > (i.context.is(s) ? e.width() : i.context.prop("scrollWidth")) || "right" === i.dir1 && i.nextpos1 + o.width() > (i.context.is(s) ? e.width() : i.context.prop("scrollWidth"))) && (i.nextpos1 = i.firstpos1, i.nextpos2 += i.addpos2 + ("undefined" == typeof i.spacing2 ? 25 : i.spacing2), i.addpos2 = 0), i.animation && i.nextpos2 < p) switch (i.dir2) {
                        case "down":
                            c.top = i.nextpos2 + "px";
                            break;
                        case "up":
                            c.bottom = i.nextpos2 + "px";
                            break;
                        case "left":
                            c.right = i.nextpos2 + "px";
                            break;
                        case "right":
                            c.left = i.nextpos2 + "px"
                    } else "number" == typeof i.nextpos2 && o.css(r, i.nextpos2 + "px");
                    switch (i.dir2) {
                        case "down":
                        case "up":
                            o.outerHeight(!0) > i.addpos2 && (i.addpos2 = o.height());
                            break;
                        case "left":
                        case "right":
                            o.outerWidth(!0) > i.addpos2 && (i.addpos2 = o.width())
                    }
                    if ("number" == typeof i.nextpos1)
                        if (i.animation && (a > i.nextpos1 || c.top || c.bottom || c.right || c.left)) switch (i.dir1) {
                            case "down":
                                c.top = i.nextpos1 + "px";
                                break;
                            case "up":
                                c.bottom = i.nextpos1 + "px";
                                break;
                            case "left":
                                c.right = i.nextpos1 + "px";
                                break;
                            case "right":
                                c.left = i.nextpos1 + "px"
                        } else o.css(h, i.nextpos1 + "px");
                    switch ((c.top || c.bottom || c.right || c.left) && o.animate(c, {
                        duration: this.options.position_animate_speed,
                        queue: !1
                    }), i.dir1) {
                        case "down":
                        case "up":
                            i.nextpos1 += o.height() + ("undefined" == typeof i.spacing1 ? 25 : i.spacing1);
                            break;
                        case "left":
                        case "right":
                            i.nextpos1 += o.width() + ("undefined" == typeof i.spacing1 ? 25 : i.spacing1)
                    }
                }
                return this
            }
        },
        queuePosition: function(t, s) {
            return i && clearTimeout(i), s || (s = 10), i = setTimeout(function() {
                PNotify.positionAll(t)
            }, s), this
        },
        cancelRemove: function() {
        	if ("closing" !== this.state) {
	            return this.timer && window.clearTimeout(this.timer), "closing" === this.state && (this.elem.stop(!0), this.state = "open", this.animating = "in", this.elem.css("height", "auto").animate({
	                width: this.options.width,
	                opacity: this.options.opacity
	            }, "fast")), this
        	}
        },
        queueRemove: function() {
            var t = this;
            return this.cancelRemove(), this.timer = window.setTimeout(function() {
                t.remove(!0)
            }, isNaN(this.options.delay) ? 0 : this.options.delay), this
        }
    }), t.extend(PNotify, {
        notices: [],
        removeAll: function() {
            t.each(PNotify.notices, function() {
                this.remove && this.remove()
            })
        },
        positionAll: function(s) {
            i && clearTimeout(i), i = null, t.each(PNotify.notices, function() {
                var t = this.options.stack;
                t && (t.nextpos1 = t.firstpos1, t.nextpos2 = t.firstpos2, t.addpos2 = 0, t.animation = s)
            }), t.each(PNotify.notices, function() {
                this.position()
            })
        },
        styling: {
            jqueryui: {
                container: "ui-widget ui-widget-content ui-corner-all",
                notice: "ui-state-highlight",
                notice_icon: "ui-icon ui-icon-info",
                info: "",
                info_icon: "ui-icon ui-icon-info",
                success: "ui-state-default",
                success_icon: "ui-icon ui-icon-circle-check",
                error: "ui-state-error",
                error_icon: "ui-icon ui-icon-alert"
            },
            bootstrap2: {
                container: "alert",
                notice: "",
                notice_icon: "icon-exclamation-sign",
                info: "alert-info",
                info_icon: "icon-info-sign",
                success: "alert-success",
                success_icon: "icon-ok-sign",
                error: "alert-error",
                error_icon: "icon-warning-sign"
            },
            bootstrap3: {
                container: "alert",
                notice: "alert-warning",
                notice_icon: "glyphicon glyphicon-exclamation-sign",
                info: "alert-info",
                info_icon: "glyphicon glyphicon-info-sign",
                success: "alert-success",
                dark: "alert-dark",
                success_icon: "glyphicon glyphicon-ok-sign",
                dark_icon: "glyphicon glyphicon-bullhorn",
                error: "alert-danger",
                error_icon: "glyphicon glyphicon-warning-sign"
            }
        }
    }), PNotify.styling.fontawesome = t.extend({}, PNotify.styling.bootstrap3), t.extend(PNotify.styling.fontawesome, {
        notice_icon: "fa fa-exclamation-circle",
        info_icon: "fa fa-info",
        success_icon: "fa fa-check",
        error_icon: "fa fa-warning"
    }), document.body ? n() : t(n), PNotify
});