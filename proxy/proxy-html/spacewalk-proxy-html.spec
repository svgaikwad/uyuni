Name: spacewalk-proxy-html
Summary: The HTML component for Spacewalk Proxy
Group:   Applications/Internet
License: GPLv2
# This src.rpm is cannonical upstream
# You can obtain it using this set of commands
# git clone git://git.fedorahosted.org/git/spacewalk.git/
# cd proxy/proxy-html
# make test-srpm
URL:     https://fedorahosted.org/spacewalk 
Source0: %{name}-%{version}.tar.gz
Version: 0.4
Release: 1%{?dist}
BuildRoot: %{_tmppath}/%{name}-root-%(%{__id_u} -n)
BuildArch: noarch
Obsoletes: rhns-proxy-html <= 5.2
Requires: httpd

%define htmldir %{_var}/www/html

%description
This package contains placeholder html pages, which the Spacewalk Server
display, if you navigate to it using your browser.

%prep
%setup -q

%build
#nothing to do here

%install
rm -rf $RPM_BUILD_ROOT
install -m 755 -d $RPM_BUILD_ROOT%{htmldir}
install -m 755 -d $RPM_BUILD_ROOT%{htmldir}/_rhn_proxy
install -m 644 _rhn_proxy/* $RPM_BUILD_ROOT%{htmldir}/_rhn_proxy/

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,apache)
%attr(755,root,apache) %dir %{htmldir}
%attr(755,root,apache) %dir %{htmldir}/_rhn_proxy
%attr(644,root,apache) %config %{htmldir}/_rhn_proxy/index.html
%attr(644,root,apache) %{htmldir}/_rhn_proxy/*.ico
%attr(644,root,apache) %{htmldir}/_rhn_proxy/*.png

%changelog
* Thu Nov  5 2008 Miroslav Suchy <msuchy@redhat.com>
- rebuild due BZ 470009

* Mon Sep  8 2008 Miroslav Suchy <msuchy@redhat.com> 0.2-1
- change graphics to Spacewalk style

* Thu Aug  7 2008 Miroslav Suchy <msuchy@redhat.com> 0.1-2
- rename to spacewalk-proxy-html

* Wed Jul 30 2008 Jan Pazdziora
- discontinue the use of external version file

* Thu May 15 2008 Miroslav Suchy <msuchy@redhat.com> 5.2.0-5
- Fix attr of files

* Fri Apr 11 2008 Miroslav Suchy <msuchy@redhat.com>
- Isolate from rhns-proxy

