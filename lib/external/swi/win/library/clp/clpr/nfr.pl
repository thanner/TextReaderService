/*  $Id: nfr.pl,v 1.1 2004/12/15 16:12:30 jan Exp $

    Part of CPL(R) (Constraint Logic Programming over Reals)

    Author:        Leslie De Koninck
    E-mail:        Tom.Schrijvers@cs.kuleuven.ac.be
    WWW:           http://www.swi-prolog.org
		   http://www.ai.univie.ac.at/cgi-bin/tr-online?number+95-09
    Copyright (C): 2004, K.U. Leuven and
		   1992-1995, Austrian Research Institute for
		              Artificial Intelligence (OFAI),
			      Vienna, Austria

    This software is part of Leslie De Koninck's master thesis, supervised
    by Bart Demoen and daily advisor Tom Schrijvers.  It is based on CLP(Q,R)
    by Christian Holzbaur for SICStus Prolog and distributed under the
    license details below with permission from all mentioned authors.

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    As a special exception, if you link this library with other files,
    compiled with a Free Software compiler, to produce an executable, this
    library does not by itself cause the resulting executable to be covered
    by the GNU General Public License. This exception does not however
    invalidate any other reasons why the executable file might be covered by
    the GNU General Public License.
*/


:- module(nfr,
	[
		transg/3
	]).

:- use_module(nf,
	[
		nf2term/2
	]).

% transg(Goal,[OutList|OutListTail],OutListTail)
%
% puts the equalities and inequalities that are implied by the elements in Goal
% in the difference list OutList
%
% called by geler.pl for project.pl

transg(resubmit_eq(Nf)) -->
	{
		nf2term([],Z),
		nf2term(Nf,Term)
	},
	[clpr:{Term=Z}].
transg(resubmit_lt(Nf)) -->
	{
		nf2term([],Z),
		nf2term(Nf,Term)
	},
	[clpr:{Term<Z}].
transg(resubmit_le(Nf)) -->
	{
		nf2term([],Z),
		nf2term(Nf,Term)
	},
	[clpr:{Term=<Z}].
transg(resubmit_ne(Nf)) -->
	{
		nf2term([],Z),
		nf2term(Nf,Term)
	},
	[clpr:{Term=\=Z}].
transg(wait_linear_retry(Nf,Res,Goal)) -->
	{
		nf2term(Nf,Term)
	},
	[clpr:{Term=Res},Goal]. 
